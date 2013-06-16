/*
 * Copyright 2011-2013 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kstenschke.shifter.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.kstenschke.shifter.helpers.ArrayHelper;
import com.kstenschke.shifter.helpers.TextualHelper;
import com.kstenschke.shifter.ShiftableLine;
import com.kstenschke.shifter.ShiftableWord;
import com.kstenschke.shifter.shiftertypes.CssLengthValue;
import com.kstenschke.shifter.shiftertypes.StringHtmlEncodable;
import com.kstenschke.shifter.shiftertypes.NumericValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ActionsPerformer {

	private AnActionEvent event;

	private Editor editor;
	private Document document;

	private CharSequence editorText;

	private int caretOffset;
	private SelectionModel selectionModel;
	boolean hasSelection;


	/**
	 * Constructor
	 */
	ActionsPerformer(final AnActionEvent event) {
		this.event = event;
		editor = event.getData(PlatformDataKeys.EDITOR);

		if (editor != null) {
			document = editor.getDocument();
			editorText = document.getCharsSequence();
			caretOffset = editor.getCaretModel().getOffset();
			selectionModel = editor.getSelectionModel();
			hasSelection = selectionModel.hasSelection();
		}
	}

	/**
	 * @param   shiftUp    Shift up or down?
	 */
	public void write(boolean shiftUp) {

		if (editor != null) {
//			final Document document = editor.getDocument();

			if (hasSelection) {
				//-------------------- Shift selection: sort lines alphabetically
				int offsetStart = selectionModel.getSelectionStart();
				int offsetEnd = selectionModel.getSelectionEnd();

				int lineNumberSelStart = document.getLineNumber(offsetStart);
				int lineNumberSelEnd = document.getLineNumber(offsetEnd);

				if (document.getLineStartOffset(lineNumberSelEnd) == offsetEnd) {
					lineNumberSelEnd--;
				}

				// Extract text as a list of lines
				List<String> lines = TextualHelper.extractLines(document, lineNumberSelStart, lineNumberSelEnd);

				if (lines.size() > 1) {
					// Sort lines alphabetically
					StringBuilder sortedText = TextualHelper.joinLines(this.sortLines(lines, shiftUp));
					int offsetLineStart = document.getLineStartOffset(lineNumberSelStart);
					int offsetLineEnd = document.getLineEndOffset(lineNumberSelEnd) + document.getLineSeparatorLength(lineNumberSelEnd);

					document.replaceString(offsetLineStart, offsetLineEnd, sortedText);
				} else {
					// Selection within one line
					String selectedText = TextualHelper.getSubString(editorText, offsetStart, offsetEnd);

					if (TextualHelper.isCommaSeparatedList(selectedText)) {
						String sortedList = this.sortCommaSeparatedList(selectedText, shiftUp);

						document.replaceString(offsetStart, offsetEnd, sortedList);
					} else if (TextualHelper.containsAnyQuotes(selectedText)) {
						document.replaceString(offsetStart, offsetEnd, TextualHelper.swapQuotes(selectedText));
					} else if (TextualHelper.containsAnySlashes(selectedText)) {
						document.replaceString(offsetStart, offsetEnd, TextualHelper.swapSlashes(selectedText));
					} else if (StringHtmlEncodable.isHtmlEncodable(selectedText)) {
						document.replaceString(offsetStart, offsetEnd, StringHtmlEncodable.getShifted(selectedText));
					}
				}
			} else {
				//-------------------- Shift word at caret
				VirtualFile file = FileDocumentManager.getInstance().getFile(document);
				String filename = (file != null) ? file.getName() : "";

				String word = TextualHelper.getWordAtOffset(editorText, caretOffset);

				int lineNumber = document.getLineNumber(caretOffset);
				int offsetLineStart = document.getLineStartOffset(lineNumber);
				int offsetLineEnd = document.getLineEndOffset(lineNumber);
				String line = editorText.subSequence(offsetLineStart, offsetLineEnd).toString();

				Boolean wordShifted = false;

				if (word != null && !word.isEmpty()) {
					int wordOffset = TextualHelper.getStartOfWordAtOffset(editorText, caretOffset);
					String prefixChar = TextualHelper.getCharBeforeOffset(editorText, wordOffset);
					String postfixChar = TextualHelper.getCharAfterOffset(editorText, wordOffset + word.length() - 1);

					// Identify word type and shift it accordingly
					ShiftableWord shiftableWord = new ShiftableWord(word, prefixChar, postfixChar, line, editorText, caretOffset, filename);

					// Comprehend negative values of numeric types
					if (NumericValue.isNumericValue(word) || CssLengthValue.isCssLengthValue(word)) {
						if (prefixChar.equals("-")) {
//							prefixChar  = "";
							word = "-" + word;
							wordOffset--;
						}
					}

					String newWord = shiftableWord.getShifted(shiftUp, editor);

					if (newWord != null && newWord.length() > 0 && !newWord.matches(word)) {
						// Replace word at caret by shifted one (if any)
						document.replaceString(wordOffset, wordOffset + word.length(), newWord);
						wordShifted = true;
					}
				}
				// -------------------- Word at caret wasn't identified/shifted, try shifting the whole line
				if (!wordShifted) {
					ShiftableLine shiftableLine = new ShiftableLine(line, editorText, caretOffset, filename);

					// Replace line by shifted one
					CharSequence shiftedLine = shiftableLine.getShifted(shiftUp, editor);
					if (shiftedLine != null) {
						document.replaceString(offsetLineStart, offsetLineStart + line.length(), shiftedLine);
					}
				}
			}
		}
	}

	/**
	 * @param lines
	 * @param shiftUp
	 * @return Given lines sorted alphabetically ascending / descending
	 */
	private List<String> sortLines(List<String> lines, Boolean shiftUp) {
		Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);

		if (!shiftUp) {
			Collections.reverse(lines);
		}

		return lines;
	}

	/**
	 * @param selectedText
	 * @param shiftUp
	 * @return Given comma separated list, sorted alphabetically ascending / descending
	 */
	private String sortCommaSeparatedList(String selectedText, Boolean shiftUp) {
		String[] items = selectedText.split(",(\\s)*");

		Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);

		if (!shiftUp) {
			Collections.reverse(Arrays.asList(items));
		}

		return ArrayHelper.implode(items, ", ");
	}

}
