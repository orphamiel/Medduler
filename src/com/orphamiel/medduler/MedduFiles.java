package com.orphamiel.medduler;

import java.util.Date;

public class MedduFiles implements Comparable<MedduFiles>
{
    public Date aptdate;
    public String filename;
    public String issue;
    public String locat;
    public String remarks;
    /**
     * @value 
     * Flag is 0 for normal data rows
     * <p>
     * Flag is 1 for "No appointments" and "No upcoming appointments" row
     * <p>
     * Flag is 2 for loading appointments screen
     * <p>
     * Flag is 3 for year headers
     */
    public int flag;
    
    public MedduFiles()
    {
        super();
    }
    
    public MedduFiles(Date aptdate, String filename, String issue, String locat, String remarks, int flag) 
    {
        super();
        this.aptdate = aptdate;
        this.filename = filename;
        this.issue = issue;
        this.locat = locat;
        this.remarks = remarks;
        this.flag = flag;
    }
    
    //Function to sort
    @Override
    public int compareTo(MedduFiles anotherfile)
    {
    	return aptdate.compareTo(anotherfile.aptdate);
    }
    
 
}