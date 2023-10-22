/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhs;

/**
 *
 * @author ORAH RICHARD
 */
public class StudentAcc {
    private int id;
    private String regno;
    private String yre;
    private String role;
      
  public StudentAcc(int aid, String aregno, String ayre, String arole) {
        this.id = aid;
        this.regno = aregno;
        this.yre = ayre;
        this.role = arole;

    }  
    
    public int getId() {
        return id;
    }

    public String getRegNo() {
        return regno;
    }

    public String getYear() {
        return yre;
    }

    public String getRole() {
        return role;
    }
  
  
  
  
  
  
  
  
    
    
    
}
