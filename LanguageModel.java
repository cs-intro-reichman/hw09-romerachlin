import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String wndw = "";
        char c;

        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
            wndw = wndw + in.readChar();
        }

        while (!in.isEmpty()) {
            c = in.readChar();
            
            List probs = CharDataMap.get(wndw);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(wndw, probs);
            }
            probs.update(c);

            wndw = wndw + c;
            wndw = wndw.substring(1);
        }

        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }
	

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
        int sum = 0;

        ListIterator listIterator = probs.listIterator(0);
        while (listIterator.hasNext()) {
            CharData cData = listIterator.next();
            sum += cData.count;
        }

        listIterator = probs.listIterator(0);
        double counter = 0;
        while (listIterator.hasNext()) {
            CharData cData = listIterator.next();
            cData.p = ((double) cData.count) / sum;
            counter += cData.p;
            cData.cp = counter;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		double ran = randomGenerator.nextDouble();
        for(int i=0;i<probs.getSize();i++)
        {
            if(probs.get(i).cp>r)
            return probs.get(i).chr;
        }
        return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength) return initialText;
        String res = "" + initialText;
        for (int i=1; i <= textLength; i++) {
            String wndw = res.substring(res.length() - windowLength, res.length());
            List options = CharDataMap.get(wndw);
            if (options == null) return res; // break
            res += getRandomChar(options);
        }
        return res;

	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
