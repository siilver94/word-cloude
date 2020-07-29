package WordCloude;

import org.json.JSONArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.*;

import scala.collection.Seq;

import java.util.*;

public class Main {
	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		String search = scan.nextLine();
		String url = "http://www.donga.com/news/search?query=" + search + "&x=0&y=0";

		Document doc, artclDoc;
		StringBuilder sb = new StringBuilder(); 

		try {
			
			doc = Jsoup.connect(url).get();
		
			Elements contentP = doc.select("p"); 
			Elements contentTit = contentP.select(".tit");
			

			for (Element e : contentTit) {
				;
				String artclAddr = e.select("a").attr("href");


				artclDoc = Jsoup.connect(artclAddr).get();

				String artclContent = artclDoc.select("div.article_txt").text();
				sb.append(artclContent); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan.close();

		String result = sb.toString();
	
		CharSequence normalized = result;

		normalized = TwitterKoreanProcessorJava.normalize(result);

		Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);

		tokens = TwitterKoreanProcessorJava.stem(tokens);

		List<String> javaParsed = TwitterKoreanProcessorJava.tokensToJavaStringList(tokens);

		Set<String> wordSet = new HashSet<String>();
		Map<String, Integer> wordCnt = new HashMap<String, Integer>();

		for (String s : javaParsed) {
			
			if (s.equals("으로") || s.equals("에서") || s.equals("이다") || s.equals("있다") || s.equals("일본"))
				continue;
		
			if (s.length() > 1) {
				if (wordSet.contains(s)) {
					wordCnt.put(s, 1 + wordCnt.get(s));
				} else if (wordSet.isEmpty()) {
					wordCnt.put(s, 1);
				} else {
					wordCnt.put(s, 1);
				}
				wordSet.add(s);
			}
		}

		JSONArray list = new JSONArray();
		
		for (String temp : wordSet) {
			if (wordCnt.get(temp) < 10)
				continue;
			else {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("text", temp);
				map.put("size", wordCnt.get(temp));

				list.put(map);
				
			}
		}
		System.out.println(list);
	}

}
