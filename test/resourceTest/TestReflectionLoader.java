/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resourceTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 *
 * @author Denahiro
 */
public class TestReflectionLoader {

    private Double variable=null;
    private String name=null;
    private Integer ID=null;

    public TestReflectionLoader(String filename) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        BufferedReader in=new BufferedReader(new FileReader(filename));
        String currentLine=in.readLine();
        while(currentLine!=null) {
            System.out.println(currentLine);
            String[] splitLine=currentLine.split("=");
            Field currentField = this.getClass().getDeclaredField(splitLine[0]);
            Class<?> fieldClass=currentField.getType();
            System.out.println(fieldClass);
            if(fieldClass==String.class) {
                currentField.set(this, splitLine[1]);
                System.out.println("String");
            } else if(fieldClass==Double.class) {
                currentField.set(this,Double.parseDouble(splitLine[1]));
                System.out.println("Double");
            } else if(fieldClass==Integer.class) {
                currentField.set(this,Integer.parseInt(splitLine[1]));
                System.out.println("Integer");
            }
            currentLine=in.readLine();
        }
        
    }

    @Override
    public String toString() {
        return name+","+Double.toString(variable)+","+Integer.toString(ID);
    }
}
