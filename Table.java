import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
//import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
class Table {
  private List<Record> records;
  private List<String> attributes;
  private String instanceName;
  private int padding = 15;
  private int keysUsed = 0;

  public int generateRecordKey(){
    this.keysUsed++;
    for(Record r : this.records){
      if(r.getKey().equals(String.valueOf(this.keysUsed))){
        this.keysUsed++;
      }
    }
    return this.keysUsed;
  }
  public List<Record> getAllRecords(){
    return this.records;
  }
  Table(String name){
    this.instanceName = name;
    //initialise records here
    this.records = new ArrayList<Record>();
    this.attributes = new ArrayList<String>();
  }
  public void setAttributes(String... attributes){
    for(String a : attributes){
      this.attributes.add(a);
    }
  }

  //delete a record
  public void deleteRecord(Record r){
    this.records.remove(r);
  }
  public void deleteAttribute(String col){
    for(Record r : this.records){
      r.removeEntry(this.attributes.indexOf(col));//fix
    }
    this.attributes.remove(col);
  }

  public List<String> getAttributes(){
    return this.attributes;
  }
  //inserts a new record in to the table
  public void insertRecord(Record rowToInsert){//
    this.records.add(rowToInsert);
  }

  public int getNUmberOfRecords(){
    return this.records.size();
  }
  public void changeRecord(String index, int column, String data){//
     for(Record r : this.records){
       if(r.getKey().equals(index)){
         r.setEntry(column, data);
       }
     }
  }

  //this is currently duplicated from  the record class
  // adapted from http://beginnersbook.com/2014/07/java-right-padding-a-string-with-spaces-and-zeros/
  public String rightPadding(String str, int num) {
    return String.format("%1$-" + num + "s", str);
  }

  private int lineLength(){
    return (this.attributes.size() * this.padding)
      + this.attributes.size() + 1;
  }

  public void printRecords(){
    System.out.println("\n" + "*** " + this.instanceName + " ***");
    System.out.print("|");
    for(String a : attributes){
      System.out.print(rightPadding(a, this.padding) + "|");
    }
    System.out.println();
    for(int i=0; i<lineLength(); ++i){
      System.out.print("-");
    }
    System.out.println();
    for(Record r : records){
      r.printEntries();
    }
  }

  public String getinstanceName(){
    return this.instanceName;
  }

  public Record getTableRecord(String index){
    Record temp = new Record();
    temp.entries("-1");
    for(Record r : this.records){
      if(r.getEntry(0).equals(index)){
        temp = r;
      }
    }
    return temp;
  }

  // print a particular column, by name
  public void printColumn(String col){
      for(Record r : records){
        r.printNthEntry(this.attributes.indexOf(col));
      }

  }

  //adds ** marker at the end of attributes
  //(a format I have chosen for saving)
  private List<String> AttributesWithMarkers(List<String> att){
    List<String> attributesWithMarkers = new ArrayList<String>();
    for(String a : att){
      attributesWithMarkers.add(a);
    }
    attributesWithMarkers.add("**");
    return attributesWithMarkers;// add padding
  }

  public int getNumberOfColumns(){
    return this.attributes.size();
  }

  //search a particular column for all rows containing
  //a specific entry
  public void subTable(String col, String entry){
    for(Record r : records){
      if(r.getEntry(this.attributes.indexOf(col)).equals(entry)){
        r.printEntries();
      }
    }
  }

  //save the entire table
  public void saveTable(){
    Path file = Paths.get("TableSave_" + this.instanceName);
    try {
      Files.write(file, AttributesWithMarkers(this.attributes), Charset.forName("UTF-8"));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    for(Record r : records){
      List<String> rec = r.getEntries();
      try {
        Files.write(file, rec, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
