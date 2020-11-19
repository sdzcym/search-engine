
import java.io.*;

import retrieval_model.RetrievalModel;
import retrieval_model.RetrievalModelIndri;

public class QrySopWAnd extends QrySop {

	public boolean docIteratorHasMatch(RetrievalModel r) {
		return this.docIteratorHasMatchMin(r);
	}

	public double getScore(RetrievalModel r) throws IOException {
		if (r instanceof RetrievalModelIndri) {
			return this.getScoreIndri(r);
		} else {
			throw new IllegalArgumentException(r.getClass().getName() + " doesn't support the WAND operator.");
		}
	}
	
	public double getDefaultScore(RetrievalModel r, int docid) throws IOException {
		double weight = 0.0;
        for (int i = 0; i < this.args.size(); i++) {
            weight += this.args.get(i).weight;
        }
        
        double score = 1.0;
        for (int i = 0; i < this.args.size(); i++) {
            QrySop q_i = (QrySop) this.args.get(i);
            score *= Math.pow(q_i.getDefaultScore(r, docid), q_i.weight / weight);
        }
        return score;
    }

	private double getScoreIndri(RetrievalModel r) throws IOException {
		double weight = 0.0;
        for (int i = 0; i < this.args.size(); i++) {
            weight += this.args.get(i).weight;
        }
        
        double score = 1.0;
        int docid = this.docIteratorGetMatch();
        for (int i = 0; i < this.args.size(); i ++) {
            QrySop q_i = (QrySop) this.args.get(i);
            if (q_i.docIteratorHasMatch(r) && q_i.docIteratorGetMatch() == docid) {
                score *= Math.pow(q_i.getScore(r), q_i.weight / weight);
            } else {
                score *= Math.pow(q_i.getDefaultScore(r, docid), q_i.weight / weight);
            }
        }
        return score;
    }

}
