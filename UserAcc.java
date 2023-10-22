/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhs;

/**
 *
 * @author ORAH RICHARD
 */
public class UserAcc {

    private int id;
    private String username;
    private String pass;
    private String role;

    public UserAcc(int aid, String ausername, String apass, String arole) {
        this.id = aid;
        this.username = ausername;
        this.pass = apass;
        this.role = arole;

    }   
    public int getId() {
        return id;
    }

    public String getUserName() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public String getRole() {
        return role;
    }
}
