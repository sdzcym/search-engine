
/**
 *  Copyright (c) 2018 Carnegie Mellon University.  All Rights Reserved.
 */
import java.io.*;
import java.util.*;

import support.InvList;

/**
 * The NEAR/n operator for all retrieval models.
 */
public class QryIopNear extends QryIop {

	/**
	 * the distance between arguments
	 */
	protected int operatorDistance;

	public QryIopNear(int operatorDistance) {
		this.operatorDistance = operatorDistance;
	}

	/**
	 * Evaluate the query operator; the result is an internal inverted list that may
	 * be accessed via the internal iterators.
	 * 
	 * @throws IOException Error accessing the Lucene index.
	 */
	protected void evaluate() throws IOException {
		// Create an empty inverted list. If there are no query arguments,
		// that's the final result.

		this.invertedList = new InvList(this.getField());

		if (args.size() == 0) {
			return;
		}

		// Each pass of the loop adds 1 document to result inverted list
		// until all of the argument inverted lists are depleted.
		while (true) {

			// Find the common next document id. If there is none, we're done.

			boolean matchFound = false;

			// Keep trying until a match is found or no match is possible.

			while (!matchFound) {

				// Get the docid of the first query argument.

				Qry q_0 = this.args.get(0);

				if (!q_0.docIteratorHasMatch(null))
					return;

				int docid_0 = q_0.docIteratorGetMatch();

				// Other query arguments must match the docid of the first query
				// argument

				matchFound = true;

				for (int i = 1; i < this.args.size(); i++) {
					Qry q_i = this.args.get(i);

					q_i.docIteratorAdvanceTo(docid_0);

					if (!q_i.docIteratorHasMatch(null)) { // If any argument is exhausted
						return; // there are no more matches.
					}

					int docid_i = q_i.docIteratorGetMatch();

					if (docid_0 != docid_i) { // docid_0 can't match. Try again.
						q_0.docIteratorAdvanceTo(docid_i);
						matchFound = false;
						break;
					}
				} // All docids have been processed. Done.

				if (matchFound) {

					// Create a new posting that is the union of the posting lists
					// that match the previous Docid within operator distance. Save it.

					List<Integer> positions = new ArrayList<Integer>();

					// each pass of the loop adds one location id of the first arg
					outterloop: while (true) {

						// find the loc id within operator distance in one document
						boolean nearFound = false;

						// keep trying until a match is found or no possible match
						while (!nearFound) {

							if (!((QryIop) q_0).locIteratorHasMatch())
								break outterloop;

							int locid_0 = ((QryIop) q_0).locIteratorGetMatch();
							int locid_pre = locid_0;
							nearFound = true;

							// other arguments must match the location id of the previous argument within
							// the operator distance
							for (int j = 1; j < this.args.size(); j++) {

								Qry q_j = this.args.get(j);
								((QryIop) q_j).locIteratorAdvancePast(locid_pre);

								if (!((QryIop) q_j).locIteratorHasMatch())
									break outterloop;

								int locid_j = ((QryIop) q_j).locIteratorGetMatch();
								if (locid_j - locid_pre > operatorDistance) {
									((QryIop) q_0).locIteratorAdvance();
									nearFound = false;
									break;
								}

								locid_pre = locid_j;
							}

							// find a match

							if (nearFound) {

								positions.add(locid_pre);

								// forward the loc id of every argument
								for (int i = 0; i < this.args.size(); i++) {
									Qry qry_i = this.args.get(i);
									((QryIop) qry_i).locIteratorAdvance();
								}
							}
						}
					}

					Collections.sort(positions);
					if (positions.size() > 0) {
						this.invertedList.appendPosting(docid_0, positions);
					}
					q_0.docIteratorAdvancePast(docid_0);
				}
			}

		}

	}

}
