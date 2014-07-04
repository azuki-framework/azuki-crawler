/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.crawler.parser.engine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

/**
 * @since 1.0.0
 * @version 1.0.0 2014/07/04
 * @author Kawakicchi
 */
public class SampleHtmlParse {

	public static void main(final String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			ParserDelegator pd = new ParserDelegator();
			MyParserCallback cb = new MyParserCallback();
			pd.parse(br, cb, true);
			cb.print();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static class MyParserCallback extends ParserCallback {

		private Map<String, Integer> counts;
		private long cntLine;
		private long cntWord;

		private List<WordData> words;

		public MyParserCallback() {
			counts = new HashMap<String, Integer>();
			cntLine = 0;
			cntWord = 0;
			words = new ArrayList<WordData>();
		}

		public void print() {
			//			for (String key : counts.keySet()) {
			//				System.out.println(key + " (" + counts.get(key) + ")");
			//			}

			System.out.println("##########################");
			System.out.println("Line: " + cntLine);
			System.out.println("Word: " + words.size());

			//for (WordData word : words) {
			//	System.out.println(String.format("%4d %s", word.getTotalPoint(), word.getWord()));
			//}

			System.out.println("##########################");
			Map<String, List<Map<String, Double>>> AAA = new HashMap<String, List<Map<String, Double>>>();
			for (WordData baseWord : words) {
				List<AnalyzeWordData> aws = analyze(baseWord, words);
				Map<String, Double> test = new HashMap<String, Double>();
				for (AnalyzeWordData aw : aws) {
					String word = aw.getWord().getWord();
					if (test.containsKey(word)) {
						test.put(word, test.get(word) * aw.getK());
					} else {
						test.put(word, aw.getK());
					}
				}

				List<Map<String, Double>> buf = null;
				if (AAA.containsKey(baseWord.getWord())) {
					buf = AAA.get(baseWord.getWord());
				} else {
					buf = new ArrayList<Map<String, Double>>();
					AAA.put(baseWord.getWord(), buf);
				}
				buf.add(test);
			}

			for (String key : AAA.keySet()) {
				List<Map<String, Double>> bbb = AAA.get(key);

				Map<String, Double> avg = new HashMap<String, Double>();
				for (Map<String, Double> m : bbb) {
					for (String word : m.keySet()) {
						if (avg.containsKey(word)) {
							avg.put(word, avg.get(word) + m.get(word));
						} else {
							avg.put(word, m.get(word));
						}
					}
				}
				for (String word : avg.keySet()) {
					avg.put(word, avg.get(word) / bbb.size());
				}

				// ソート
				List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(avg.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
					@Override
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						return (o1.getValue() <= o2.getValue()) ? 1 : -1;
					}
				});

				System.out.println("==================================================================================");
				System.out.println(key + "("+ AAA.get(key).size() +")");
				for (int i = 0; i < 20; i++) {
					Map.Entry<String, Double> entry = entries.get(i);
					if (key.equals(entry.getKey())) continue;
					System.out.println(String.format("%.8f %s", entry.getValue(), entry.getKey()));
				}
				//for (Map.Entry<String, Double> entry : entries) {
				//	System.out.println(String.format("%.8f %s", entry.getValue(), entry.getKey()));
				//}
			}

		}

		private List<AnalyzeWordData> analyze(final WordData aWord, final List<WordData> aWords) {
			double lineAlpha = 0.9;
			double lineBeta = 10.f;
			//double pointAlpha = 0.7;
			//double pointBeta = 100.f;

			List<AnalyzeWordData> result = new ArrayList<>();

			WordData baseWord = aWord;
			//System.out.println(baseWord.getWord());

			for (int i = 0; i < aWords.size(); i++) {
				WordData targetWord = aWords.get(i);

				double a = Math.pow(lineAlpha, Math.abs(baseWord.getLine() - targetWord.getLine()) / lineBeta);
				//double b = Math.pow(pointAlpha, Math.abs(baseWord.getTotalPoint() - targetWord.getTotalPoint()) / pointBeta);

				// System.out.println(String.format("%4d %4d %f %s", targetWord.getLine(), targetWord.getPoint(), a * b, targetWord.getWord()));
				result.add(new AnalyzeWordData(targetWord, a));
			}
			return result;
		}

		private static class AnalyzeWordData {
			private WordData word;
			private double k;

			public AnalyzeWordData(final WordData aWord, final double aK) {
				word = aWord;
				k = aK;
			}

			public WordData getWord() {
				return word;
			}

			public double getK() {
				return k;
			}
		}

		private void addWord(final String aWord) {
			if (counts.containsKey(aWord)) {
				counts.put(aWord, counts.get(aWord) + 1);
			} else {
				counts.put(aWord, Integer.valueOf(1));
			}
		}

		public void handleText(char[] data, int pos) {
			String line = String.copyValueOf(data);
			//System.out.println(line.trim());

			Tokenizer tokenizer = Tokenizer.builder().build();
			List<Token> tokens = tokenizer.tokenize(line);

			int point = 0;
			for (Token token : tokens) {
				//				System.out.println("==================================================");
				//				System.out.println("allFeatures : " + token.getAllFeatures());
				//				System.out.println("partOfSpeech : " + token.getPartOfSpeech());
				//				System.out.println("position : " + token.getPosition());
				//				System.out.println("reading : " + token.getReading());
				//				System.out.println("surfaceFrom : " + token.getSurfaceForm());
				//				System.out.println("allFeaturesArray : " + Arrays.asList(token.getAllFeaturesArray()));
				//				System.out.println("辞書にある言葉? : " + token.isKnown());
				//				System.out.println("未知語? : " + token.isUnknown());
				//				System.out.println("ユーザ定義? : " + token.isUser());

				String word = token.getAllFeaturesArray()[6];
				
				if (token.getSurfaceForm().length() == 1) continue; // TODO:

				String s = token.getAllFeaturesArray()[0].toString();
				if ("名詞".equals(s)) {
					//System.out.println(token.getSurfaceForm());
					//System.out.println("allFeaturesArray : " + Arrays.asList(token.getAllFeaturesArray()));
					if ("*".equals(word)) {
						//System.out.println("-> " + token.getSurfaceForm());
						addWord(token.getSurfaceForm());

						words.add(new WordData(token.getSurfaceForm(), cntLine, point, cntWord));
						point++;
						cntWord++;
					} else {
						//System.out.println("-> " + word);
						addWord(word);

						words.add(new WordData(word, cntLine, point, cntWord));
						point++;
						cntWord++;
					}
				} else if ("動詞".equals(s)) {
					//System.out.println("=> " + word);
					addWord(word);

					words.add(new WordData(word, cntLine, point, cntWord));
					point++;
					cntWord++;
				}

			}
			cntLine++;
		}

	}

	public static class WordData {
		private String word;
		private long line;
		private long pointOfLine;
		private long pointOfAll;

		public WordData(final String aWord, final long aLine, final long aPosition, final long aTotalPosition) {
			word = aWord;
			line = aLine;
			pointOfLine = aPosition;
			pointOfAll = aTotalPosition;
		}

		public String getWord() {
			return word;
		}

		public long getLine() {
			return line;
		}

		public long getPoint() {
			return pointOfLine;
		}

		public long getTotalPoint() {
			return pointOfAll;
		}
	}

}
