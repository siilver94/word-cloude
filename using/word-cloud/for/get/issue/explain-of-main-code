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
		// url 선언
		// http://www.donga.com/news/search?query=%EC%82%BC%EC%84%B1&x=0&y=0
		String search = scan.nextLine();
		String url = "http://www.donga.com/news/search?query=" + search + "&x=0&y=0";

		// Jsoup 얻어온 결과 HTML 전체 문서
		Document doc, artclDoc;
		// string 문자열을 더할때 새로운 객체를 생성하지 않고 기존 데이터에 더하는 방식이어서 속도도 빠르고 부하 적음
		StringBuilder sb = new StringBuilder(); // HTML 가져오기

		try {
			// HTML 코드가 doc에 저장이 됩니다.
			doc = Jsoup.connect(url).get();
			// System.out.println(doc.toString());

			// Element : Document의 HTML 요소
			// Element가 모인 자료형. for나 while 등 반복문 사용이 가능하다.
			// conctent doc에 저장된 HTML파일에서 클래스이름, id이름, 태그위치등.. 을
			// 지정하여 그부분만 빼올수 있습니다.
			Elements contentP = doc.select("p"); // p는 해당 사이트들의 객체
			// .tit 은 기사이므로 이미지 광고등을 제거
			Elements contentTit = contentP.select(".tit"); // title 클래스만 선택
			// System.out.println(contentTit);

			for (Element e : contentTit) {
				;
				String artclAddr = e.select("a").attr("href"); // a요소 안의 해당 주소(href)속성값 가졍오기(select)

				// 검색문에서 실제 기사가 있는 주소를 artclAddr로 추출하여 다시한번 Jsoup.connect를 이용하여 검색
				artclDoc = Jsoup.connect(artclAddr).get();

				String artclContent = artclDoc.select("div.article_txt").text(); // 글자만 가져오기
				sb.append(artclContent); // 끝에 추가
			}
			// System.out.println(sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan.close();

		String result = sb.toString();

		// CharSequence :<interface> 객체내 보관하는 문자열은 같은 String 클래스와 같은 유니코드라 하더라도 마크업 문자를
		// 사용하여 변형과 가공이 가능한 문자열이란 의미로 스타일 문자 또는 연속되는 문자라고 한다.
		// String 과 차이는 마크업을 쓸수 잇냐 없냐,
		// String 문자열은 마크업 문자를 입력하여 사용할 수 없는 문자열인란 의미로 변경금지 문자라 부른다.
    
		CharSequence normalized = result;

		// charSqeunce 형으로 변환한 result 를 TwitterKoreanProcessorJava 를 사용 오타도 잡아줌
		// return normalized result
		normalized = TwitterKoreanProcessorJava.normalize(result);

		// 순서 잇음반환된 것이 리스트임에 유의하라. Seq는 트레잇이다. 리스트는 Seq를 잘 구현하고 있다.
		// 여기서 볼 수 있듯 Seq라 불리는 팩토리 객체가 있어서 리스트를 만들어준다.)
		// seq 리스트 형태를 tokenize 형태로 만드는데, (형태:index, 단어길이) 품사별로 구분
		Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);

		// tokens 를 어근화 단어를 분석할 때, 실질적 의미를 나타내는 중심이 되는 부분 추출.
		// ‘덮개’의 ‘덮-’, ‘어른스럽다’의 ‘어른’ 따위이다
		tokens = TwitterKoreanProcessorJava.stem(tokens);
		// 리스트 형태로 tokens을 받아와 javapased에 저장
		List<String> javaParsed = TwitterKoreanProcessorJava.tokensToJavaStringList(tokens);

		// 단어 Counting
		Set<String> wordSet = new HashSet<String>();
		Map<String, Integer> wordCnt = new HashMap<String, Integer>();

		for (String s : javaParsed) {
			// 일반적으로 많이 등장하는 단어 : 단어 리스트에 불포함
			if (s.equals("으로") || s.equals("에서") || s.equals("이다") || s.equals("있다") || s.equals("일본"))
				continue;
			// 단어 길이가 1인 조사들 : 단어 리스트에 불포함
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
		// Json 형태로 만들기
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
