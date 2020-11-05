/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt   (compress)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    private static int R = 256;        // number of input chars
    //private static int L = 4096;       // number of codewords = 2^W
    //private static int W = 12;         // codeword width
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static final char flag = '^'; //

    public static void compress() {
     
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        //initialize the current string
        StringBuilder current = new StringBuilder();
        //read and append the first char
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {
            codeword = st.get(current);
           
            c = BinaryStdIn.readChar();
            current.append(c);

            if(!st.contains(current)){
              BinaryStdOut.write(codeword, W);
              if (code < L)    // Add to symbol table if not full
                  st.put(current, code++);
            else {    
                    if( W < 16)
                    {
                        W++;
                        L *= 2;
                        st.put(current, code++); //new add
                        //System.out.println(); //Look at this if statement
                    }
                }
                current = current.delete(0, current.length());
              //current = new StringBuilder();
              current.append(c);
            }
        }

        BinaryStdOut.write(st.get(current), W); //Write EOF

        BinaryStdOut.write(R, W); //Write EOF
        BinaryStdOut.close();
    }


    public static void expand() {
        // this is the codebook
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
        
        while (true) {
            if(i >= L && W < 16)                                    // variable codeword size implementation
            {
                /*
                if(i == L-1) {
                    st[i++] = val + s.charAt(0);
                }
                */
                W++;
                L *= 2;
                //L = 2*L;
                st = resizeArr(st);
               // st[i++] = val + s.charAt(0);
            }
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];    // index out of bounds
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            /*
            if(i >= L && W < 16)                                    // variable codeword size implementation
            {
                /*
                if(i == L-1) {
                    st[i++] = val + s.charAt(0);
                }
                
                W++;
                L *= 2;
                //L = 2*L;
                //st = resizeArr(st);
               // st[i++] = val + s.charAt(0);
            }
           */
            if (i < L) 
                st[i++] = val + s.charAt(0);     
            val = s;

        }
        BinaryStdOut.close();
    }
    private static String[] resizeArr(String[] arr) {

		String[] newStr = new String[arr.length * 2];	

		for (int i = 0; i < arr.length; i++) 
			newStr[i] = arr[i];

		return newStr;
	}

    // Resets Dictionary
    // Only to be used for compression
    //public static void reset() 
        // set flag val '^' in first line of file (only 1 bit)
            // aka BinaryStdOut.write(flag, 1);
        // 

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        
            // if args[1].equalsIgnoreCase("r")
            // call reset method
            // if args[1].equalsIgnoreCase("n")
            // carry on as normal
            // ONLY FOR COMPRESSION
        // if flag "^" is present in output file, reset if running out of codewords
        
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
