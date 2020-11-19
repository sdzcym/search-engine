import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import support.InvList;

public class QryIopWindow extends QryIop {

	protected int operatorDistance;

	public QryIopWindow(int operatorDistance) {
		this.operatorDistance = operatorDistance;
	}

	protected void evaluate() throws IOException {

		this.invertedList = new InvList(this.getField());

		if (args.size() == 0) {
			return;
		}

		while (this.docIteratorHasMatchAll(null)) {

			QryIop q_0 = (QryIop) this.args.get(0);

			int start = -this.args.size();
			int size = this.args.size();
			int[] locs = new int[size];

			for (int i = 0; i < size; i++) {
				QryIop q_i = (QryIop) this.args.get(i);
				int docid = q_i.docIteratorGetMatch();
				start += q_i.docIteratorGetMatchPosting().tf;
				q_i.docIteratorAdvanceTo(docid);
				locs[i] = q_i.locIteratorGetMatch();
				q_i.locIteratorAdvance();
			}

			int index = 0;

			List<Integer> positions = new ArrayList<>();

			boolean matchFound = false;

			while (index < start) {
				int min_index = this.match(locs);
				if (min_index == -1) {
					int max = Integer.MIN_VALUE;			
					for (int i = 0; i < locs.length; i++) {
						if (locs[i] > max) max = locs[i];
					}
					positions.add(max);
					for (int i = 0; i < locs.length; i++) {
						QryIop q_i = (QryIop) this.args.get(i);
						if (q_i.locIteratorHasMatch()) {		
							locs[i] = q_i.locIteratorGetMatch();
							q_i.locIteratorAdvance();
							index++;
						} else {
							index = start;
							matchFound = true;
							break;
						}
					}
				} else {
					QryIop q = (QryIop) this.args.get(min_index);
					
					if (q.locIteratorHasMatch()) {
						locs[min_index] = q.locIteratorGetMatch();
						q.locIteratorAdvance();
						index++;
					} else {
						break;
					}
				}
			}

			// check match again.
			if (this.match(locs) == -1 && !matchFound) {
				int max = Integer.MIN_VALUE;
				for (int i = 0; i < locs.length; i++) {
					if (locs[i] > max) max = locs[i];
				}
				positions.add(max);
			}

			// add matched locations to the inverted list
			if (positions.size() != 0) {
				this.invertedList.appendPosting(q_0.docIteratorGetMatch(), positions);
			}

			// advance the doc and continue
			q_0.docIteratorAdvancePast(q_0.docIteratorGetMatch());
		}
	}
	
	/**
	 * Check if there is a match
	 * 
	 * @param locs An array contains a set of locations (loc id)
	 * @return the minimum index if there is no match, otherwise return -1
	 * 
	 * @throws IOException Error accessing the Lucene index.
	 */
	private int match(int[] locs) {
		
		// save the minimum value of locs in the array
		int min = Integer.MAX_VALUE;
		// save the maximum value of locs in the array
		int max = Integer.MIN_VALUE;
		
		// save the index of the minimum value
		int min_index = 0;
		
		for (int i = 0; i < locs.length; i++) {

			// find the minimum value and its index
			if (locs[i] < min) {
				min = locs[i];
				min_index = i;
			}

			// find the maximum value
			if (locs[i] > max) {
				max = locs[i];
			}
		}

		// return the index of the minimum value if there is no match as #window
		if (max - min >= this.operatorDistance) {
			return min_index;
		}
		
		// return -1, if there is a match
		return -1;
	}

}
