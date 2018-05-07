package hw3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ratings") 
class ratings {

		private int
		critics_score,
		audience_score;


		public int getCritics_score() {
			return critics_score;
		}

		public void setCritics_score(int critics_score) {
			this.critics_score = critics_score;
		}

		public int getAudience_score() {
			return audience_score;
		}

		public void setAudience_score(int audience_score) {
			this.audience_score = audience_score;
		}
	
}
