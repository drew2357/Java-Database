import java.io.*;
import java.util.*;
import java.util.ArrayList;
class Record {
  private List<String> entries;
  private int key;


  Record(){
    entries = new ArrayList<String>();
  }

  public void entries(String... entries){
    for(String e : entries){
      this.entries.add(e);
    }
  }

  public void printNthEntry(int n){
    System.out.println(entries.get(n));
  }

  public void printEntries(){
    System.out.print("|");
    for(String e : entries){
      System.out.print(rightPadding(e, 15) + "|");
    }
    System.out.println();
  }
  //this is currently duplicated from  the table class
  // adapted from http://beginnersbook.com/2014/07/java-right-padding-a-string-with-spaces-and-zeros/
  private String rightPadding(String str, int num) {
    return String.format("%1$-" + num + "s", str);
  }

  public String getKey(){
    return this.entries.get(0);
  }

  public String getEntry(int i){
    return this.entries.get(i);
  }
  public void setEntry(int i, String data){
    this.entries.set(i, data);
  }
  public void addEntry(String data){
    this.entries.add(data);
  }

  public int getNUmberOfEntries(){
    return this.entries.size();
  }
  //return a list of strings
  public List<String> getEntries(){
    return this.entries;
  }

  public String[] getEntriesArray(){
    String array[] = new String[this.entries.size()];
    for(int i =0 ; i < this.entries.size(); i++){
      array[i] = this.entries.get(i);
    }
    return array;
  }
  void removeEntry(int i){
    this.entries.remove(i);
  }
}
