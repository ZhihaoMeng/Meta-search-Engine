package common.entities.searchresult;

public class RelateSearchWord {
	private String word;
	private int num;
	public RelateSearchWord(String word, int num) {
		super();
		this.word = word;
		this.num = num;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	@Override
	public String toString() {
		return "RelateSearchWord [word=" + word + ", num=" + num + "]";
	}
	
}
