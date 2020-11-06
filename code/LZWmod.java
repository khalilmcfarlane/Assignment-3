import java.util.*;
import java.io.*;
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
    private static boolean RESET_FLAG = false;
    private static String option;     //Which mode for compression option

    public static void compress() {
     
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        //writing in a flag at beginning of output file
        //Before decompression, program will read flag and decide to reset
        if(RESET_FLAG == true) {
            BinaryStdOut.write(RESET_FLAG);
        }
        else
            BinaryStdOut.write(0, 1);
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
                    if(RESET_FLAG == true && W == 16)
                    {
                        //Need to reinitialize everything
                        st = new TSTmod<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put(new StringBuilder("" + (char) i), i);
                        code = R+1;  // R is codeword for EOF
                        W = 9;
                        L = 512;
                        st.put(current, code++);
                    }    
                    if( W < 16)
                    {
                        W++;
                        L *= 2;
                        st.put(current, code++); 
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
        boolean reset_character = BinaryStdIn.readBoolean();
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
            
            if(i >= L && W == 16 && reset_character == true) {
                //reset codebook
                st = new String[L];

                // initialize symbol table with all 1-character strings
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";                        // (unused) lookahead for EOF
                W = 9;
                L = 512;
                i = R+1;

            }
            
            if(i >= L && W < 16 && reset_character == false) {                               
                W++;
                L *= 2;
                st = resizeArr(st);
            }
            
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];    // index out of bounds
            if (i == codeword) s = val + val.charAt(0);   // special case hack
    
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

    public static void main(String[] args) {
        try
        {
            if(args[1].equalsIgnoreCase("r")) {
                option = args[1];
               RESET_FLAG = true;
            }
            if(args[1].equalsIgnoreCase("n")) {
                option = args[1];
                RESET_FLAG = false;
            }
        }
        catch (ArrayIndexOutOfBoundsException a) {};
        if (args[0].equals("-")) compress();
        
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
