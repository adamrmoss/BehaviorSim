package sim.ui.method;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import sim.model.entity.Category;

/*
 * EJE - version 2.7 - "Everyone's Java Editor"
 * 
 * Copyright (C) 2003 Claudio De Sio Cesari
 * 
 * Require JDK 1.4
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * Info, Questions, Suggestions & Bugs Report to eje@claudiodesio.com
 *  
 */

public class EJEArea extends JEditorPane {

	// public static void main(String[] args) throws Exception
	// {
	// JavaInterface.engineRef.init("C:\\Users\\qfs\\Projects\\workspace\\behaviorsim");
	// EJEArea textArea = new EJEArea(null);
	// Document document = textArea.getDocument();
	// try {
	// FileReader fileReader = new FileReader(new
	// java.io.File("C:\\Users\\qfs\\Projects\\workspace\\behaviorsim\\src\\sim\\ui\\method\\JVM.java"));;
	// BufferedReader bufferedReader = new BufferedReader(fileReader);
	// String line = bufferedReader.readLine();
	//			
	// try {
	// document.insertString(0, "public void main(String[] args){\n" +
	// "System.out.println();\n" +
	// "}", null);
	// }catch(Exception e){}
	//			
	// // while (line != null) {
	// // try {
	// // String nextLine = bufferedReader.readLine();
	// // if (nextLine != null)
	// // line += "\n";
	// // Element element = document.getDefaultRootElement();
	// // int rowCount = element.getElementCount();
	// // document.insertString(rowCount/*textArea.getCaretPosition()*/, line,
	// // null);
	// // line = nextLine;
	// // } catch (BadLocationException blExc) {
	// // blExc.printStackTrace();
	// // }
	// // }
	// fileReader.close();
	// bufferedReader.close();
	// } catch (IOException exc) {
	// exc.printStackTrace();
	// }
	// JScrollPane sp = new JScrollPane( textArea );
	// JViewport port = sp.getViewport();
	// port.setOpaque(true);
	// port.setBackground(Color.red);
	// port.setView(textArea);
	// port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
	// JFrame apiDocsFrame = new JFrame("Test");
	// apiDocsFrame.setContentPane(sp);
	// apiDocsFrame.setSize(800, 600);
	// // apiDocsFrame.setLocationRelativeTo(EJE.getEJE());
	// apiDocsFrame.show();
	// textArea.setCaretPosition(0);
	// String text = textArea.getText();
	// }

	/**
	 * 
	 */
	private static final long serialVersionUID = 7574748579544446190L;

	/*
	 * public static final String FIRST_CHARACTER_IDENTIFIER =
	 * "[[a-z]|[A-Z]|_|$]";//[^\\d]
	 * 
	 * public static final String NOT_FIRST_CHARACTER_IDENTIFIER = "[" +
	 * FIRST_CHARACTER_IDENTIFIER + "|\\d]";
	 * 
	 * public static final String IDENTIFIER = "("+FIRST_CHARACTER_IDENTIFIER +
	 * "{1}" + ""+NOT_FIRST_CHARACTER_IDENTIFIER + "*)";
	 * 
	 * public static final String IMPORT_KEYWORD = "import"; public static final
	 * String IMPORT_DECLARATION = IMPORT_KEYWORD+"\\s+((" + IDENTIFIER +
	 * "|\\*)\\.?)*;";
	 */
	public static final String KEYWORD_COLOR = "eje_area.keyword_color";

	public static final String COMMON_WORD_COLOR = "eje_area.common_word_color";

	public static final String MULTI_LINE_COMMENT_COLOR = "eje_area.multi_line_color";

	public static final String SINGLE_LINE_COMMENT_COLOR = "eje_area.single_line_color";

	public static final String STRING_LITERAL_COLOR = "eje_area.string_literal_color";

	public static final String CHAR_LITERAL_COLOR = "eje_area.char_literal_color";

	public static final String NUMERIC_LITERAL_COLOR = "eje_area.numeric_literal_color";

	public static final String OPERATOR_COLOR = "eje_area.operator_color";

	public static final String INDENT_ON_INSERT_BREAK = "indentOnInsertBreak";

	public static final String OS_NAME = System.getProperty("os.name");

	private JavaInterface javaInterface;

	private ClassWizard classWizard;

	protected PopupListener popupListener;

//	private int rowNumber;

	protected Category category;

	protected static HashSet classPathForReflection = new HashSet();

	// Initialize the class path
	static {
		String JAVA_HOME = System.getProperty("java.home");
		JAVA_HOME = JAVA_HOME.replace(File.separatorChar, '/');
		addClassPathForReflection(JAVA_HOME + "/lib/", "rt.jar");
	}

	private DocumentListener documentListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
			int caretPosition = getCaretPosition();
			fireDocumentEvent(caretPosition);
		}

		public void insertUpdate(DocumentEvent e) {
			// internalText = EJEArea.this.getText();
			// System.out.println(internalText);
			int caretPosition = getCaretPosition();
			fireDocumentEvent(caretPosition + 1);
		}

		private void fireDocumentEvent(int caretPosition) {
			if (classWizard.isVisible()) {
				Element row = getRowAt(caretPosition);
				int firstColumnInRow = row.getStartOffset();
				int end = row.getEndOffset();
				try {
					String text = getText(firstColumnInRow,
							(end - firstColumnInRow));
					String memberName = classWizard.getPopupListener()
							.getLastTokenForSelect(text);
					classWizard.setSelectedMember(memberName);
				} catch (BadLocationException exc) {
					// System.out.println("In Insert update");
					exc.printStackTrace();
				}
			}
		}
	};

	public EJEArea(Category category) {
		try {
			setupKeymap();
			setOpaque(true);
			setBackground(Color.white);
			setForeground(Color.darkGray);
			setEditable(true);
			setIndentOnBreak(true);
			setKeywordColor(Color.blue.darker());
			setCommonWordColor(Color.black/* new Color(9,20,115) */);
			setCharLiteralColor(new Color(0, 102, 0));
			setMultiLineCommentColor(Color.red);
			setNumericLiteralColor(new Color(0, 102, 0));
			setSingleLineCommentColor(Color.red);
			setStringLiteralColor(new Color(0, 102, 0));
			setOperatorColor(Color.lightGray.darker().darker());
			setDragEnabled(true);
			// refsMap = new EJEMap();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		try {
			classWizard = new ClassWizard("java.lang.Object",
					ClassWizard.OBJECT); // Fake class for
			// init...
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		setPredefined(category);

		this.setOpaque(true);
		popupListener = new PopupListener();
		this.addKeyListener(popupListener);
		this.getDocument().addDocumentListener(documentListener);

	}

	/**
	 * Set predefined category
	 */
	public void setPredefined(Category c) {
		category = c;
	}

	private void setupKeymap() {
		Keymap map = JTextComponent.getKeymap("EJEKeymap");
		if (map == null) {
			Keymap parent = getKeymap();
			map = JTextComponent.addKeymap("EJEKeymap", parent);
			KeyStroke insertBreakKeyStroke = KeyStroke.getKeyStroke(
					KeyEvent.VK_ENTER, 0, false);
			map.addActionForKeyStroke(insertBreakKeyStroke,
					new JavaEditorKit.InsertBreakAction());
		}
		setKeymap(map);
	}

	protected EditorKit createDefaultEditorKit() {
		return new JavaEditorKit();
	}

	public void setIndentOnBreak(boolean b) {
		putClientProperty(INDENT_ON_INSERT_BREAK, new Boolean(b));
	}

	public boolean getIndentOnBreak() {
		Boolean b = (Boolean) getClientProperty(INDENT_ON_INSERT_BREAK);
		if (b != null)
			return b.booleanValue();
		else
			return false;
	}

	public static void addClassPathForReflection(String baseDirectory,
			String jarFileName) {
		try {
			java.net.URL url = new java.net.URL("jar", "", baseDirectory
					+ jarFileName + "!/");
			classPathForReflection.add(url);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static java.net.URL getClassPathForReflection()[] {
		int size = classPathForReflection.size();
		java.net.URL[] urls = new java.net.URL[size];
		Iterator iterator = classPathForReflection.iterator();
		for (int i = 0; iterator.hasNext(); ++i) {
			urls[i] = (java.net.URL) iterator.next();
			// System.out.println(urls[i]);
		}
		return urls;
	}

	public void setKeywordColor(Color c) {
		putClientProperty(KEYWORD_COLOR, c);
	}

	public Color getKeywordColor() {
		return (Color) getClientProperty(KEYWORD_COLOR);
	}

	public void setCommonWordColor(Color c) {
		putClientProperty(COMMON_WORD_COLOR, c);
	}

	public Color getCommonWordColor() {
		return (Color) getClientProperty(COMMON_WORD_COLOR);
	}

	public void setCharLiteralColor(Color c) {
		putClientProperty(CHAR_LITERAL_COLOR, c);
	}

	public Color getCharLiteralColor() {
		return (Color) getClientProperty(CHAR_LITERAL_COLOR);
	}

	public void setMultiLineCommentColor(Color c) {
		putClientProperty(MULTI_LINE_COMMENT_COLOR, c);
	}

	public Color getMultiLineCommentColor() {
		return (Color) getClientProperty(MULTI_LINE_COMMENT_COLOR);
	}

	public void setNumericLiteralColor(Color c) {
		putClientProperty(NUMERIC_LITERAL_COLOR, c);
	}

	public Color getNumericLiteralColor() {
		return (Color) getClientProperty(NUMERIC_LITERAL_COLOR);
	}

	public void setSingleLineCommentColor(Color c) {
		putClientProperty(SINGLE_LINE_COMMENT_COLOR, c);
	}

	public Color getSingleLineCommentColor() {
		return (Color) getClientProperty(SINGLE_LINE_COMMENT_COLOR);
	}

	public void setStringLiteralColor(Color c) {
		putClientProperty(STRING_LITERAL_COLOR, c);
	}

	public Color getStringLiteralColor() {
		return (Color) getClientProperty(STRING_LITERAL_COLOR);
	}

	public void setOperatorColor(Color c) {
		putClientProperty(OPERATOR_COLOR, c);
	}

	public Color getOperatorColor() {
		return (Color) getClientProperty(OPERATOR_COLOR);
	}
	
	
	
	
	
	

	public class ClassWizard extends JPopupMenu implements
			java.awt.event.MouseListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 607998350441860053L;

		private JScrollPane scroll;

		private JList list;

		private static final int CLASS = 0;

		private static final int OBJECT = 1;

		private static final int ARRAY = 2;

		private static final int PACKAGE = 3;

		private static final int METHOD = 4;

		private static final int ATTRIBUTE = 5;

		public ClassWizard(JavaInterface ji) {
			init(ji);
		}

		public void init(JavaInterface ji) {
			javaInterface = ji;
			list = new JList(javaInterface);
			list.setCellRenderer(javaInterface.new ClassWizardCellRenderer());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.getSelectionModel().setAnchorSelectionIndex(4);
			list.addMouseListener(this);
			
			this.setOpaque(true);
			this.setLayout(new BorderLayout());
			
			scroll = new JScrollPane();
			scroll.getViewport().setView(list);
			this.add(scroll, BorderLayout.CENTER);
		}

		public ClassWizard(String className, int type)
				throws ClassNotFoundException {
			switch (type) {
			case OBJECT:
				javaInterface = new ObjectInterface(className);
				break;
			case CLASS:
				javaInterface = new ClassInterface(className);
				break;
			case ARRAY:
				javaInterface = new ArrayInterface(className);
				break;
			case PACKAGE:
				// to be done
				break;
			case METHOD:
				// to be done
				break;
			case ATTRIBUTE:
				// to be done
				break;
			}
			init(javaInterface);
		}

		public Dimension getPreferredSize() {
			int length = list.getModel().getSize();
			Dimension d = new Dimension(super.getPreferredSize().width,
					length < 8 ? length * 19 : super.getPreferredSize().height);
			return d;
		}

		public void selectNextIndex() {
			int index = list.getSelectedIndex();
			int visibleIndexGap = 3;
			int lastIndex = getListSize() - 2; // Last useful index
			index += 1; // new index
			int gap = lastIndex - index;
			if (gap < 3) {
				visibleIndexGap = gap + 1;
				if (gap < 0) {
					index = lastIndex;
					visibleIndexGap = 0;
				}
			}
			highlightIndex(index, index + visibleIndexGap);
		}
		
		/**
		 * Highlight the first word beginning with the specified
		 * string. Also, bring the first word to be the first one
		 * visible to the user. If no matched word found, no action
		 * will be taken.
		 * 
		 * @param str The string the word begins with
		 */
		public void highlightWordBeginWith(String str)
		{
//			ListModel lm = list.getModel();
//			int num = lm.getSize();
//			for (int i = 0; i < num; i++)
//			{				
//				Object mem = (Object)lm.getElementAt(i);
//				String name = mem.toString();
//				if (name.startsWith(str))
//				{
//					int visibleIndexGap = 3;
//					int index = i-1;
//					if (index < 0) {
//						index = 0;
//						visibleIndexGap = 0;
//					}
//					highlightIndex(index, index-visibleIndexGap);
//					return;
//				}
//			}
		}

		public void selectPreviousIndex() {
			int index = list.getSelectedIndex();
			int visibleIndexGap = 3;
			index -= 1;
			if (index < 0) {
				index = 0;
				visibleIndexGap = 0;
			}
			highlightIndex(index, index - visibleIndexGap);
		}

		public void selectNextPageIndex() {
			int index = list.getSelectedIndex();
			int visibleIndexGap = 3;
			int lastIndex = getListSize() - 2; // Last useful index
			index += 8; // new index
			int gap = lastIndex - index;
			if (gap < 3) {
				visibleIndexGap = gap + 1;
				if (gap < 0) {
					index = lastIndex;
					visibleIndexGap = 0;
				}
			}
			highlightIndex(index, index + visibleIndexGap);
		}

		public void selectPreviousPageIndex() {
			int index = list.getSelectedIndex();
			int visibleIndexGap = 3;
			index -= 8;
			if (index < 0) {
				index = 0;
				visibleIndexGap = 0;
			}
			highlightIndex(index, index - visibleIndexGap);
		}

		public void selectHomeIndex() {
			highlightIndex(0, 0);
		}

		public void selectEndIndex() {
			int lastIndex = getListSize() - 2; // Last useful index
			highlightIndex(lastIndex, lastIndex);
		}

		private int getListSize() {
			ListModel lm = list.getModel();
			return lm.getSize();
		}

		private void highlightIndex(int index, int visibleIndex) {
			list.setSelectedIndex(index);
			list.ensureIndexIsVisible(visibleIndex);
		}

		public void setSelectedMember(String memberName) {
			if (memberName == null)
				return;
			ListModel lm = list.getModel();
			int size = lm.getSize();
			String item = "";
			for (int i = 0; i < size; ++i) {
				Object ret = lm.getElementAt(i);
				item = ret.toString();
				if (item.startsWith(memberName)) {
					break;
				}
			}
			list.setSelectedValue(item, true);
		}

		public PopupListener getPopupListener() {
			return popupListener;
		}

		public String getSelectedValue() {
			Object ret = list.getSelectedValue();
			return ret.toString();
		}

		/**
		 * Invoked when the mouse button has been clicked (pressed and released)
		 * on a component.
		 */
		public void mouseClicked(MouseEvent e) {
			// Double click
			if (e.getClickCount() == 2) {
				popupListener.fireReturnPressedWithClassWizardVisible();
			}
		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e) {
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse enters a component.
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse exits a component.
		 */
		public void mouseExited(MouseEvent e) {
		}
	}
	
	
	
	
	

	class PopupListener extends KeyAdapter {
		private int caretPosition;

		public void keyPressed(KeyEvent e) {
			char keyChar = e.getKeyChar();
			switch (keyChar) {
			case '.':
				fireDotPressed(e);
				break;
			case '\n':
				fireReturnPressed(e);
				break;
			case '@':
				fireSysFunctionPressed(e);
				break;
			case '\t':
				e.consume();
				fireTabPressed();
			case '(':
			case ';':
				if (classWizard.isVisible())
					classWizard.setVisible(false);
				break;
			case 8:
				int caretPosition = EJEArea.this.getCaretPosition();
				try {
					String lastChar = EJEArea.this
							.getText(caretPosition - 1, 1);
					if (classWizard.isVisible() && lastChar.equals("."))
						classWizard.setVisible(false);
				} catch (BadLocationException exc) {
					// exc.printStackTrace();
				}
				break;
			default:
				if (classWizard.isVisible()) {
					keyPressedWithVisibleClassWizard(e);
				}
			}
		}

		// REFACTOR
		private void fireDotPressed(KeyEvent e) {
			if (classWizard.isVisible()) {
				classWizard.setVisible(false);
			}
			String invoker = null;
			String className = null;
			try {
				invoker = getInvoker(); // retrieve the previous word
				if (invoker != null) {
					if (invoker.equals("this")) {
						className = getFileName();
						try {
							// System.out
							// .println("verifying if this class has a package");
							Class.forName(className, false,
									new java.net.URLClassLoader(
											getClassPathForReflection()));
							// System.out
							// .println("verifying if class has no package");
							classWizard = new ClassWizard(className,
									ClassWizard.OBJECT);
							showClassWizard(e);
							return;
						} catch (ClassNotFoundException ex) {
							// System.out
							// .println("verifying if class has a package as import");
							className = verifyImports(className);
							classWizard = new ClassWizard(className,
									ClassWizard.OBJECT);
							showClassWizard(e);
							return;
						}
					}

					if (invoker.equals("super")) {
						className = getSuperClassName();
						classWizard = new ClassWizard(className,
								ClassWizard.OBJECT);
						showClassWizard(e);
						return;
					}
					// check if invoker is a class
					try {
						Class.forName(invoker, false,
								new java.net.URLClassLoader(
										getClassPathForReflection()));
						// IS A CLASS IN DIRECTORY OR A FULLY QUALIFIED NAME
						classWizard = new ClassWizard(invoker,
								ClassWizard.CLASS);
						showClassWizard(e);
						return;
					} catch (ClassNotFoundException ex) {
						// System.out.println("Class name: " + invoker
						// + " not found, verifying imports");
						try {
							// if (className.indexOf("(") != -1) {
							// throw new
							// FileNotFoundException("This is a method: " +
							// className);
							// }
							className = verifyImports(invoker);
							// IS A CLASS IN PACKAGE
							classWizard = new ClassWizard(className,
									ClassWizard.CLASS);
							showClassWizard(e);
							return;
						} catch (FileNotFoundException exc) {
							// check if invoker is a reference... searching for
							// its class
							try {
								// System.out.println(invoker
								// + " is not an imported Class");
								// check if invoker is an array and eventually
								// return
								if (isArray(invoker)) {
									classWizard = new ClassWizard(invoker,
											ClassWizard.ARRAY);
									showClassWizard(e);
									return;
								}
								// extract class of invoker
								className = extractClass(invoker);
								if (className != null) {
									// System.out.println("Class name: "
									// + className);
									classWizard = new ClassWizard(className,
											ClassWizard.OBJECT);
									showClassWizard(e);
								}
							} catch (ClassNotFoundException cnfExc) {
								// System.out
								// .println("Reference's class not found or Reference not declared...");
								try {
									className = verifyImports(className);
									// System.out.println("Class name: "
									// + className);
									classWizard = new ClassWizard(className,
											ClassWizard.OBJECT);
									showClassWizard(e);
								} catch (FileNotFoundException fnfExc) {
									// System.out.println(fnfExc
									// + " Incorrect Package!");
								} catch (IOException ioExc) {
									ioExc.printStackTrace();
								} catch (BadLocationException blExc) {
									// System.out.println("In verify Import"
									// + blExc.offsetRequested());
									exc.printStackTrace();
								} catch (Exception nex) {
									nex.printStackTrace();
								}
							}
						}
					}
				}
			} catch (BadLocationException exc) {
				// System.out.println("Offset requested : "
				// + exc.offsetRequested());
				exc.printStackTrace();
			} catch (FileNotFoundException exc) {
				// System.out.println(exc + " class not found: " + className);
				// try if invoker is an external member
				tryIfInvokerIsAnExternalMember(invoker, e);
			} catch (NullPointerException exc) {
				tryIfInvokerIsAnExternalMember(invoker, e);
				// System.out.println("Exception?!?!");
				exc.printStackTrace();
			} catch (Exception exc) {
				tryIfInvokerIsAnExternalMember(invoker, e);
				// System.out.println("Exception?!?!");
				exc.printStackTrace();
			}
		}

		private void tryIfInvokerIsAnExternalMember(String invoker, KeyEvent e) {
			StringTokenizer tokenizer = new StringTokenizer(invoker, ".");
			int tokens = tokenizer.countTokens();
			// if (tokens < 2) {
			// System.out.println("The last dot is not applicable for "
			// + invoker);
			// return;
			// }
			List tokensList = new ArrayList();
			while (tokenizer.hasMoreTokens()) {
				tokensList.add(tokenizer.nextToken());
			}
			// take the first token
			String className = null;
			Class classObject = null;
			// ////////////////////////////////
			// for (int i = 0; i < tokens; i++) {
			String token = (String) tokensList.get(0);
			try {
				// }
				if (token != null) {
					if (token.equals("this")) {
						className = getFileName();
						try {
							// System.out
							// .println("verifying if this class has a package");
							classObject = Class.forName(className, false,
									new java.net.URLClassLoader(
											getClassPathForReflection()));

						} catch (ClassNotFoundException ex) {
							// System.out
							// .println("verifying if class has a package as import");
							// TODO eval this
							className = verifyImports(className);
							classObject = Class.forName(className, false,
									new java.net.URLClassLoader(
											getClassPathForReflection()));
						}
					}

					else if (token.equals("super")) {
						className = getSuperClassName();
						classObject = Class.forName(className, false,
								new java.net.URLClassLoader(
										getClassPathForReflection()));
					} else {
						// check if invoker is a class
						try {
							classObject = Class.forName(token, false,
									new java.net.URLClassLoader(
											getClassPathForReflection()));
							// IS A CLASS IN DIRECTORY OR A FULLY QUALIFIED NAME
						} catch (ClassNotFoundException ex) {
							// System.out.println("Class name: " + token
							// + " not found, verifying imports");
							try {
								className = verifyImports(token);
								// IS A CLASS IN PACKAGE
								classObject = Class.forName(className, false,
										new java.net.URLClassLoader(
												getClassPathForReflection()));
							} catch (FileNotFoundException exc) {
								// check if invoker is a reference... searching
								// for
								// its class
								try {
									// System.out.println(token
									// + " is not an imported Class");
									// extract class of invoker
									if (token.equals(")")) {
										throw new ClassNotFoundException();
									}
									className = extractClass(token);
									if (className != null) {
										// System.out.println("Class name: "
										// + className);
										classObject = Class
												.forName(
														className,
														false,
														new java.net.URLClassLoader(
																getClassPathForReflection()));
									}
								} catch (ClassNotFoundException cnfExc) {
									// System.out
									// .println("Reference's class not found or Reference not declared...");
									try {
										className = verifyImports(className);
										// System.out.println("Class name: "
										// + className);
										classObject = Class
												.forName(
														className,
														false,
														new java.net.URLClassLoader(
																getClassPathForReflection()));
									} catch (FileNotFoundException fnfExc) {
										// System.out.println(fnfExc
										// + " Incorrect Package!");
									} catch (IOException ioExc) {
										ioExc.printStackTrace();
									} catch (BadLocationException blExc) {
										// System.out.println("In verify Import"
										// + blExc.offsetRequested());
										exc.printStackTrace();
									} catch (Exception nex) {
										nex.printStackTrace();
									}
								}
							}
						}
					}
				}
				String type = null;
				int memberType = -1;
				for (int i = 1; i < tokens; i++) {
					String currentToken = (String) tokensList.get(i);
					if (currentToken.indexOf("(") != -1) {
						memberType = ClassWizard.METHOD;
					} else if (currentToken.indexOf("[") != -1) {
						memberType = ClassWizard.ARRAY;
					} else {
						memberType = ClassWizard.ATTRIBUTE;
					}
					try {
						classObject = Class.forName(type);
					} catch (RuntimeException exc) {
						exc.printStackTrace();
					}
					type = getMemberType(currentToken, classObject, memberType);
				}
				classWizard = new ClassWizard(type,
				/* memberType */ClassWizard.OBJECT);
				showClassWizard(e);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		private String getMemberType(String currentToken, Class classObject,
				int memberType) {
			String type = null;
			switch (memberType) {
			case ClassWizard.METHOD:
				type = getMethodReturnType(currentToken, classObject);
				break;
			case ClassWizard.ATTRIBUTE:
				type = getAttributeType(currentToken, classObject);
				break;
			case ClassWizard.ARRAY:
				type = getArrayType(currentToken, classObject);
				break;
			}
			return type;
		}

		private String getArrayType(String currentToken, Class classObject) {
			// TODO da modificare per capirne il tipo per un elemento dell'array

			return null;
		}

		private String getMethodReturnType(String currentToken,
				Class classObject) {
			Method[] methods = classObject.getMethods();
			currentToken = currentToken.substring(0, currentToken
					.lastIndexOf('('));
			for (int i = 0; i < methods.length; i++) {
				// System.out.println("$="+methods[i].getName());
				if (currentToken.equals(methods[i].getName())) {
					return methods[i].getReturnType().getName();
				}
			}
			return null;
		}

		private String getAttributeType(String currentToken, Class classObject) {
			Field[] fields = classObject.getFields();
			for (int i = 0; i < fields.length; i++) {
				if (currentToken.equals(fields[i].getName())) {
					return fields[i].getType().getName();
				}
			}
			return null;
		}

		private String getSuperClassName() throws ClassNotFoundException,
				FileNotFoundException, IOException, BadLocationException {
			String className = null;
			className = getFileName();
			try {
				System.out.println("verifying if class has a package");
				Class thisClass = Class
						.forName(className, false, new java.net.URLClassLoader(
								getClassPathForReflection()));
				System.out.println("verifying if class has no package");
				Class superClass = thisClass.getSuperclass();
				return superClass.getName();
			} catch (ClassNotFoundException ex) {
				System.out
						.println("verifying if class has a package as import");
				className = verifyImports(className);
				Class thisClass = Class
						.forName(className, false, new java.net.URLClassLoader(
								getClassPathForReflection()));
				Class superClass = thisClass.getSuperclass();
				return superClass.getName();
			}
		}

		private String getFileName() {
			String fileName = null;
			try {
				fileName = getClassName("\\bclass\\b");
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				try {
					fileName = getClassName("\\binterface\\b");
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				} catch (NullPointerException e1) {
					try {
						fileName = getClassName("\\benum\\b");
					} catch (BadLocationException e2) {
						e2.printStackTrace();
					} catch (NullPointerException e2) {
						e1.printStackTrace();
					}
					e1.printStackTrace();
				}
			}
			return fileName;
		}

		public String getClassName(String pattern) throws BadLocationException {
			Pattern p = Pattern.compile(pattern);
			String content = EJEArea.this.getText();
			Matcher m = p.matcher(content);
			String className = null;
			if (m.find()) {
				int end = m.end();
				// ///////////////////
				int count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, end)
						: 0;
				// System.out.println("COUNT " + count);
				int veryEnd = end - count;
				Element row = getRowAt(veryEnd);
				// ///////////////////
				int lastColumnInRow = row.getEndOffset();
				String rowSegment = EJEArea.this.getText(veryEnd,
						lastColumnInRow - (veryEnd));
				className = getNextToken(rowSegment);
			}
			System.out.println("Name=" + className);
			return className;
		}

		private String getNextToken(String rowSegment) {
			StringTokenizer st = new StringTokenizer(rowSegment,
					";. \n\"()[]$|!,+-/=?^:<>\f\'\t\r%&~ ", false);
			String nextToken = null;
			if (st.hasMoreTokens()) {
				nextToken = st.nextToken();
			}
			return nextToken;
		}

		private boolean isArray(String refString) throws BadLocationException {
			Pattern p = Pattern.compile("\\b" + refString + "\\b");
			String content = EJEArea.this.getText();
			Matcher m = p.matcher(content);
			while (m.find()) {
				int start = m.start();
				// /////////////
				int count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, start)
						: 0;
				// ///////////
				return isArray(refString, start - count);
			}
			return false;
		}

		private boolean isArray(String refString, int position)
				throws BadLocationException {
			Element row = getRowAt(position);
			int firstColumnInRow = row.getStartOffset();
			int lastColumnInRow = row.getEndOffset();
			String rowSegment = EJEArea.this.getText(firstColumnInRow,
					lastColumnInRow - firstColumnInRow);
			Pattern braces = Pattern.compile("\\[]");
			Matcher m = braces.matcher(rowSegment);
			if (m.find()) {
				return true;
			}
			return false;
		}

		private void keyPressedWithVisibleClassWizard(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_UP) {
				classWizard.selectPreviousIndex();
			} else if (keyCode == KeyEvent.VK_DOWN) {
				classWizard.selectNextIndex();
			} else if (keyCode == KeyEvent.VK_PAGE_UP) {
				classWizard.selectPreviousPageIndex();
			} else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
				classWizard.selectNextPageIndex();
			} else if (keyCode == KeyEvent.VK_HOME) {
				classWizard.selectHomeIndex();
			} else if (keyCode == KeyEvent.VK_END) {
				classWizard.selectEndIndex();
			} else if (keyCode == KeyEvent.VK_ESCAPE
					|| keyCode == KeyEvent.VK_SPACE
					|| keyCode == KeyEvent.VK_TAB) {
				classWizard.setVisible(false);
			} else {
				classWizard.highlightWordBeginWith(String.valueOf(e.getKeyChar()));
			}
			e.consume();
		}

		private void fireSysFunctionPressed(KeyEvent e) {
			// Close the class wizard if it is still open
			if (classWizard.isVisible()) {
				classWizard.setVisible(false);
			}
			// Construct a new class wizard to display system functions, and
			// user-defined functions & variables.
			try {
				classWizard = new ClassWizard(new SysFuncInterface(category));
				showClassWizard(e);
			} catch (FileNotFoundException fnfExc) {
				// System.out.println(fnfExc
				// + " Incorrect Package!");
			} catch (IOException ioExc) {
				ioExc.printStackTrace();
			} catch (BadLocationException blExc) {
				// System.out.println("In verify Import"
				// + blExc.offsetRequested());
				blExc.printStackTrace();
			} catch (Exception nex) {
				nex.printStackTrace();
			}
		}

		private void fireReturnPressed(KeyEvent e) {
			if (classWizard.isVisible()) {
				e.consume();
				fireReturnPressedWithClassWizardVisible();
			}
		}

		private void fireReturnPressedWithClassWizardVisible() {
			String selectedValue = classWizard.getSelectedValue();
			if (selectedValue.equals("<<no member>>")) {
				classWizard.setVisible(false);
				return;
			}
			try {
				Document doc = EJEArea.this.getDocument();
				int length = 0;
				int caretPosition = EJEArea.this.getCaretPosition();
				String lastChar = EJEArea.this.getText(caretPosition - 1, 1);
				classWizard.setVisible(false);
				if (!lastChar.equals(".")) {
					Element row = getRowAt(caretPosition);
					int firstColumnInRow = row.getStartOffset();
					String rowContent = EJEArea.this.getText(firstColumnInRow,
							caretPosition - firstColumnInRow);
					String memberString = getLastToken(rowContent);
					length = memberString.length();
					doc.remove(caretPosition - length, length);
				}
				selectedValue = classWizard.getSelectedValue();
				// Check whether it is a method. If it is, check whether it is a
				// user-defined method.
				if (selectedValue.indexOf("(") != -1) {
					// User-defined method
					if (selectedValue.startsWith("0")) {
						selectedValue = selectedValue.substring(1);
					}
				}
				try {
					int inx = selectedValue.lastIndexOf(" ");
					selectedValue = selectedValue.substring(0, inx);
				} catch (Exception e) {
				}
				// int index = selectedValue.lastIndexOf('(') + 1;
				// if (index == 0) {
				// index = selectedValue.indexOf(' ');
				// }
				// selectedValue = (selectedValue.lastIndexOf(')') == index ?
				// selectedValue
				// .substring(0, index + 1)
				// : selectedValue.substring(0, index));
				doc.insertString(caretPosition - length, selectedValue, null);
			} catch (BadLocationException exc) {
				// System.out.println(exc.offsetRequested());
				exc.printStackTrace();
			}
		}

		private void fireTabPressed() {
			try {
				Document doc = EJEArea.this.getDocument();
				int caretPosition = EJEArea.this.getCaretPosition();
				doc.insertString(caretPosition, "    ", null);
			} catch (BadLocationException exc) {
				// System.out.println(exc.offsetRequested());
				exc.printStackTrace();
			}
		}

		private void showClassWizard(KeyEvent e) throws Exception {
			Point p = EJEArea.this.getCaret().getMagicCaretPosition();
			if (p == null)
				return;
			int x = (int) p.getX();
			int y = (int) p.getY();
			int classWizardWidth = classWizard.getPreferredSize().width;
			int classWizardHeight = classWizard.getPreferredSize().height;
			int fontHeight = EJEArea.this.getFont().getSize();
			int widthLimit = EJEArea.this.getSize().width - classWizardWidth;
			int heightLimit = EJEArea.this.getSize().height
					- (classWizardHeight + 2);
			if (x >= widthLimit)
				x = x - classWizardWidth;
			if (y >= heightLimit)
				y -= (classWizardHeight + fontHeight);
			classWizard.show(e.getComponent(), x, (y + fontHeight + 2));
			EJEArea.this.requestFocus();
		}

		private String extractClass(String refString)
				throws BadLocationException, FileNotFoundException {
			// System.out.println("Trying to extract class of : " + refString);
			Pattern p = Pattern.compile("\\b" + refString + "\\b");
			String content = EJEArea.this.getText(0, getCaretPosition());
			Matcher m = p.matcher(content);
			String className = null;
			// Searching for a true Class!
			while (m.find()) {
				int start = m.start();
				int count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, start)
						: 0;
				String proposedClassName = getClassName(start + count);
				// System.out.println("Found in position:" + (start + count)
				// + ", Class to search: " + proposedClassName);
				if (proposedClassName != null) {
					try {
						Class.forName(proposedClassName, false,
								new java.net.URLClassLoader(
										new java.net.URL[] {}));
						className = proposedClassName;
						// return className;
					} catch (ClassNotFoundException e) {
						// System.out.println("Class name: " + proposedClassName
						// + " not found, verifying imports");
						try {
							proposedClassName = verifyImports(proposedClassName);
							className = proposedClassName;
							// return className;
						} catch (FileNotFoundException exc) {
							// System.out.println(className
							// + " is not an imported Class");
							continue;
						} catch (Exception exc) {
							// System.out.println("Exception?!?!");
							e.printStackTrace();
						}
					}
				}
			}
			// if (className == null) {
			// System.out.println("Class not found: " + className);
			// }
			return className;
		}

		private String getInvoker() throws BadLocationException {
			caretPosition = EJEArea.this.getCaretPosition();
			String invoker = getInvokerOrClassName(caretPosition);
			return invoker;
		}

		private String getInvoker(int caretPosition)
				throws BadLocationException {
			// TODO torna alla precedente parentesi di apertura RELATIVA e
			// prendi il lasttoken
			
			int nestedBrace = 0;
			int index = 0;
			for (index = caretPosition - 2; index > 0; index--) {
				String character = EJEArea.this.getText(index, 1);
				if (character != null && character.equals(")")) {
					nestedBrace++;
				} else if (character != null && character.equals("(")) {
					nestedBrace--;
					if (nestedBrace == -1) {
						break;
					}
				}
			}
			String args = EJEArea.this.getText(index, caretPosition - index);
			return getInvokerOrClassName(index) + args;
		}

		private String getClassName(int caretPosition)
				throws BadLocationException {
			return getInvokerOrClassName(caretPosition);
		}

		private String getInvokerOrClassName(int caretPosition)
				throws BadLocationException {
			this.caretPosition = caretPosition;
			Element row = getRowAt(caretPosition);
			int firstColumnInRow = row.getStartOffset();
			String rowSegment = EJEArea.this.getText(firstColumnInRow,
					caretPosition - firstColumnInRow);
			return getLastTokenNoDot(rowSegment);
		}

		private String getLastTokenNoDot(String rowSegment)
				throws BadLocationException {
			int indexOf = -1;
			// This is done to partially (without arguments) understand generics
			if ((indexOf = rowSegment.indexOf('<')) != -1
					&& rowSegment.indexOf('>') != -1) {
				// System.out.println("Recursive call to getLastTokenNoDot: "
				// + rowSegment.substring(0, indexOf));
				return getLastTokenNoDot(rowSegment.substring(0, indexOf));
			}
			StringTokenizer st = new StringTokenizer(rowSegment,
					";\n\"[]$|!,+-/=?^:<>\f\'\t\r%&~( ", false);// <<<<<
			String lastToken = null;
			while (st.hasMoreTokens()) {
				lastToken = st.nextToken();
				if (lastToken.equals(")")) {// ////////////////////////////////////////////////////
					lastToken = getInvoker(caretPosition);
				}
			}
			// System.out.println("last token=" + lastToken);
			try {
				lastToken.indexOf(".");
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			// try {
			// return index !=
			// -1?lastToken.substring(index+1,(lastToken.length() - index) + 1
			// ):lastToken;
			// } catch (StringIndexOutOfBoundsException e) {
			// e.printStackTrace();
			// return lastToken;
			// }
			return lastToken;
		}

		private String getLastToken(String rowSegment)
				throws BadLocationException {
			StringTokenizer st = new StringTokenizer(rowSegment,
					";. \n\"()[]$|!,+-/=?^:<>\f\'\t\r%&~ ", false);
			String lastToken = null;
			while (st.hasMoreTokens()) {
				lastToken = st.nextToken();
			}
			return lastToken;
		}

		private String getLastTokenForSelect(String rowSegment)
				throws BadLocationException {
			StringTokenizer st = new StringTokenizer(rowSegment,
					";. \n\"()[]$|!,+-/=?^:<>\f\'\t\r%&~ ", true);
			String lastToken = null;
			while (st.hasMoreTokens()) {
				String tmpLastToken = st.nextToken();
				if (!tmpLastToken.equals("\n")) {
					lastToken = tmpLastToken;
				}
			}
			return lastToken;
		}

		private String verifyImports(String classToFind)
				throws FileNotFoundException, IOException, BadLocationException {
			String classInPackage = null;
			if ((classInPackage = verifyPackage(classToFind)) != null) {
				return classInPackage;
			}
			// System.out.println("Verifying imports...");
			Pattern p = Pattern.compile("\\bimport\\b");
			// Pattern p = Pattern.compile(IMPORT_DECLARATION);
			String content = EJEArea.this.getText();
			Matcher m = p.matcher(content);
			// java.lang.* is always imported
			try {
				String langClass = "java.lang." + classToFind;
				Class.forName(langClass);
				return langClass;
			} catch (ClassNotFoundException e) {
				// System.out.println(classToFind + "is not a java.lang class");
			}
			while (m.find()) {
				// /////////////////|||||||||||||||||||
				int start = m.start();
				int count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, start)
						: 0;
				JavaDocument doc = (JavaDocument) getDocument();
				System.out.println("COUNT ROWS=" + count + "START=" + start);
				boolean inComment = false;
				try {
					inComment = doc.getCommentStatus(start - count);
					if (inComment) {
						// System.out.println("continue!");
						continue;
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				// /////////////////|||||||||||||||||||
				int end = m.end();
				count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, end)
						: 0;
				// System.out.println("Keyword import found at line " + end);
				String importLine[] = getImportLine(end - count);
				String packageName = importLine[0];
				String className = importLine[1];
				if (className.equals(classToFind))
					return packageName + "." + className;
				else if (className.equals("*")) {
					try {
						Class.forName(packageName + "." + classToFind);
						return packageName + "." + classToFind;
					} catch (ClassNotFoundException e) {
						System.out.println(packageName + "." + classToFind
								+ "is not a true class!");
					}
				}
			}
			throw new FileNotFoundException("Class not found: " + classToFind);
		}

		private String verifyPackage(String className)
				throws FileNotFoundException, IOException, BadLocationException {
			// System.out.println("Verifying if class is in package...");
			Pattern p = Pattern.compile("\\bpackage\\b");
			String content = EJEArea.this.getText();
			Matcher m = p.matcher(content);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				// //////////////////////////
				int count = OS_NAME.toLowerCase().startsWith("win") ? getWindowsReturnGap(
						content, end)
						: 0;
				JavaDocument doc = (JavaDocument) getDocument();
				// System.out.println("COUNT ROWS=" + count + "START=" + start);
				Element root = doc.getDefaultRootElement();
				int firstParagraph = root.getElementIndex(start);
				boolean inComment = false;
				try {
					inComment = doc.getCommentStatus(firstParagraph);
					if (inComment) {
						// System.out.println("continue!");
						continue;
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				// //////////////////////////
				// System.out.println("Keyword package found at line " + end);
				// String packageName = importLine[0];
				Element row = getRowAt(end - count);
				int lastColumnInRow = row.getEndOffset();
				String packageName = getLastTokenNoDot(EJEArea.this.getText(
						end, lastColumnInRow - end));
				// System.out.println("packageName=" + packageName);
				try {
					Class.forName(packageName + "." + className, false,
							new java.net.URLClassLoader(new java.net.URL[] {}));
					return packageName + "." + className;
				} catch (ClassNotFoundException e) {
					// System.out.println(className +
					// " is not in this package");
				}
			}
			return null;
		}

		private String[] getImportLine(int importEndOffset)
				throws BadLocationException {
			// Too much text to scan...limit to import
			Element row = getRowAt(importEndOffset);
			int lastColumnInRow = row.getEndOffset();
			String[] importLine = new String[2];
			importLine[1] = getLastToken(EJEArea.this.getText(importEndOffset,
					lastColumnInRow - importEndOffset));
			int packageEndOffset = lastColumnInRow
					- (importLine[1].length() + 3);
			int firstColumnInRow = row.getStartOffset();
			importLine[0] = getLastTokenNoDot(EJEArea.this.getText(
					firstColumnInRow, packageEndOffset - firstColumnInRow));

			// importLine[0] = EJEArea.this.getText(importEndOffset,
			// packageEndOffset - importEndOffset).trim();
			return importLine;
		}
	}

//	private Element getCurrentRow() {
//		return getRowAt(getCaretPosition());
//	}

	private Element getRowAt(int offset) {
		Element element = getDocument().getDefaultRootElement();
		int rowNumber = element.getElementIndex(offset);
		return element.getElement(rowNumber);
	}

	public String getPackageName() {
		try {
			String content = EJEArea.this.getText();
			StringReader rd = new StringReader(content);
			StreamTokenizer st = new StreamTokenizer(rd);
			st.parseNumbers();
			st.slashSlashComments(true);
			st.slashStarComments(true);
			int token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				token = st.nextToken();
				switch (token) {
				case StreamTokenizer.TT_WORD:
					// A word was found; the value is in sval
					String word = st.sval;
					if ("package".equals(word)) {
						return word;
					}
					break;
				case StreamTokenizer.TT_EOF:
					// End of file has been reached
					break;
				default:
					// A regular character was found; the value is the token
					// itself
//					char ch = (char) st.ttype;
					break;
				}
				rd.close();
			}
		} catch (IOException e) {
		}
		return "";
	}

	// public String getPackageName() {
	// String packageName = "";
	// Pattern p = Pattern.compile("\\bpackage\\b");
	// String content = EJEArea.this.getText();
	// Matcher m = p.matcher(content);
	// while (m.find()) {
	// int start = m.start();
	// int end = m.end();
	// int count = EJE.OS_NAME.toLowerCase().startsWith("win") ?
	// getWindowsReturnGap(
	// content, end)
	// : 0;
	// JavaDocument doc = (JavaDocument) getDocument();
	// System.out.println("COUNT ROWS=" + count + "START=" + start);
	// Element root = doc.getDefaultRootElement();
	// int firstParagraph = root.getElementIndex(start);
	// boolean inComment = false;
	// try {
	// System.out.println("firstParagraph:"+firstParagraph);
	// inComment = doc.getCommentStatus(start);
	// if (inComment) {
	// System.out.println("continue!");
	// continue;
	// }
	// } catch (Exception exc) {
	// exc.printStackTrace();
	// }
	// System.out.println("Keyword package found at character " + end);
	// Element row = getRowAt(end - count);
	// int lastColumnInRow = row.getEndOffset();
	// try {
	// packageName = getLastToken(EJEArea.this.getText(end,
	// lastColumnInRow - end));
	// break;
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// System.out.println("packageName=" + packageName);
	// }
	/*
	 * StringTokenizer st = new StringTokenizer(getText(), ";
	 * \n\"()[]$|!,+-/=?^: <>\f\'\t\r%&~ ", false); String token = ""; try {
	 * while (st.hasMoreTokens()) { if ("package".equals(st.nextToken())) {
	 * token = st.nextToken(); return token; } } } catch (Exception exc) {
	 * exc.printStackTrace(); } return token;
	 */
	// return packageName;
	// }
//	private String getLastToken(String rowSegment) throws BadLocationException {
//		int slashSlashIndex = rowSegment.indexOf("//");
//		int slashStarIndex = rowSegment.indexOf("/*");
//		int starSlashIndex = rowSegment.indexOf("*/");
//		if (slashSlashIndex != -1) {
//			rowSegment = rowSegment.substring(0, slashSlashIndex);
//		} else if (slashStarIndex != -1) {
//			try {
//				rowSegment = rowSegment.substring(0, slashStarIndex)
//						+ rowSegment.substring(starSlashIndex, rowSegment
//								.length());
//			} catch (StringIndexOutOfBoundsException exc) {
//				rowSegment = rowSegment.substring(0, slashStarIndex);
//			}
//		}
//		StringTokenizer st = new StringTokenizer(rowSegment, "; ", false);
//		String lastToken = "";
//		if (st.hasMoreTokens()) {
//			lastToken = st.nextToken();
//		}
//		System.out.println("last token=" + lastToken);
//		return lastToken;
//	}

	public int getWindowsReturnGap(String content, int end) {
		String part = content.substring(0, end);
		Pattern partPattern = Pattern.compile("\n");
		Matcher partMatcher = partPattern.matcher(part);
		int count = 0;
		while (partMatcher.find()) {
			count++;
		}
		return count;
	}
}