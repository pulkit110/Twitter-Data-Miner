/**
 * 
 */
package twitter.dataanalyzer.utils;

import com.aliasi.spell.TfIdfDistance;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ObjectToCounterMap;

/**
 * @author pulkit and sapan
 * 
 */
public class LingPipeSimilarity {

	/**
	 * @param strings
	 */
	public static void main(String[] args) {
		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		TfIdfDistance tfIdf = new TfIdfDistance(tokenizerFactory);

		String[] strings = new String[3];
		
		strings[0] = "Bush destroyed Iraq";
		strings[1] = "Iraq was destroyed by Bush";
		strings[2] = "Bush was the worst president";
		
		
		for (String s : strings) {
	        tfIdf.handle(s);
		}
		
		ObjectToCounterMap<String> a = tfIdf.termFrequencyVector("president");
		
		System.out.printf("\n  %18s  %8s  %8s\n", "Term", "Doc Freq", "IDF");
		for (String term : tfIdf.termSet())
			System.out.printf("  %18s  %8d  %8.2f\n", term,
					tfIdf.docFrequency(term), tfIdf.idf(term));

		for (String s1 : strings) {
			for (String s2 : strings) {
				System.out.println("\nString1=" + s1);
				System.out.println("String2=" + s2);
				System.out.printf("distance=%4.2f  proximity=%4.2f\n",
						tfIdf.distance(s1, s2), tfIdf.proximity(s1, s2));
			}
		}
	}

}