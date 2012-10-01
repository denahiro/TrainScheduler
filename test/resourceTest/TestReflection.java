/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package resourceTest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Denahiro
 */
public class TestReflection {

    public static void main(String[] args) {
        try {
            TestReflectionLoader t = new TestReflectionLoader("test.txt");
            System.out.println(t);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(TestReflection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestReflection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TestReflection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TestReflection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
