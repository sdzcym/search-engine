
import java.io.*;

import retrieval_model.RetrievalModel;
import retrieval_model.RetrievalModelIndri;
import retrieval_model.RetrievalModelRankedBoolean;
import retrieval_model.RetrievalModelUnrankedBoolean;

public class QrySopAnd extends QrySop {

	public boolean docIteratorHasMatch(RetrievalModel r) {
		if (r instanceof RetrievalModelIndri) {
            return this.docIteratorHasMatchMin(r);
        }
        return this.docIteratorHasMatchAll(r);
	}

	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelUnrankedBoolean) {
			return this.getScoreUnrankedBoolean(r);
		} else if (r instanceof RetrievalModelRankedBoolean) {
			return this.getScoreRankedBoolean(r);
		} else if (r instanceof RetrievalModelIndri) {
            return this.getScoreIndri(r);
        } else {
			throw new IllegalArgumentException(r.getClass().getName() + " doesn't support the AND operator.");
		}
	}

	private double getScoreUnrankedBoolean(RetrievalModel r) throws IOException {
		if (!this.docIteratorHasMatchCache()) {
			return 0.0;
		} else {
			return 1.0;
		}
	}

	private double getScoreRankedBoolean(RetrievalModel r) throws IOException {
		if (!this.docIteratorHasMatchCache()) {
			return 0.0;
		} else {
			double minScore = Double.MAX_VALUE;
	        for (int i = 0; i < this.args.size(); i++) {
	            double score = ((QrySop) this.args.get(i)).getScore(r);
	            minScore = minScore > score ? score : minScore;
	        }
	        return minScore;
		}
	}
	
	public double getDefaultScore(RetrievalModel r, int docid) throws IOException {
        double score = 1.0;
        for (int i = 0; i < this.args.size(); i ++) {
            score *= ((QrySop) this.args.get(i)).getDefaultScore(r, docid);
        }
        return Math.pow(score, 1.0 / this.args.size());
    }

	private double getScoreIndri(RetrievalModel r) throws IOException {
        double score = 1.0;
        int docid = this.docIteratorGetMatch();
        int size = this.args.size();
        
        for (int i = 0; i < size; i ++) {
            QrySop q_i = ((QrySop) this.args.get(i));
            if (q_i.docIteratorHasMatch(r) && q_i.docIteratorGetMatch() == docid){
            	score *= q_i.getScore(r);
            }else {
            	score *= q_i.getDefaultScore(r, docid);
            }
        }
        return Math.pow(score, 1.0 / size);
    }

}
