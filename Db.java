/*java database coursework*/
import java.io.*;
import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

class Db {
  private List<Table> tables;
  //int keysUsed = 0;

  private void printTables(){
    for(Table t : this.tables){
      System.out.println();
      t.printRecords();
    }
  }
  private void printTableNames(){
    System.out.println("\nTable Names");
    System.out.println("-----------");
    for(Table t : this.tables){
      System.out.println("-" + t.getinstanceName());
    }
  }

  private File[] getLocalFiles(){
    Path dbPath = Paths.get("");
    String p = dbPath.toAbsolutePath().toString();
    File[] currentFiles = new File(p).listFiles();
    return currentFiles;
  }

  private void loadLocalFiles()throws IOException{
    for(File f : getLocalFiles()){
      if(f.toString().contains("TableSave")){
        readTable(f);
      }
      if(f.toString().contains("DBinstructions")){
        Scanner input = new Scanner(new File(f.toString()));
        while (input.hasNextLine()){
          System.out.println(input.nextLine());
        }
      }
    }
    printTableNames();
  }

  //read in a table from a trext file
  private void readTable(File f)throws IOException {//input type file
    boolean inColumns = true;
    int columnCounter = 0;
    int rowEntriesCount = 0;//total number of entries in the table
    int rowCheck = 0;//number of rows
    boolean markerFound = false;
    Table rTable;
    rTable = new Table(f.getName().replace("TableSave_",""));
    Record rRecord = new Record();
    rTable.insertRecord(rRecord);
    List<String> lines = Files.readAllLines(Paths.get(f.toString()));
    for (String line : lines) {
      if(inColumns == false){
        if(rowEntriesCount%columnCounter == 0){
          if(rowCheck > 0){
            rRecord = new Record();
            rTable.insertRecord(rRecord);
          }
          rowCheck++;
        }
        rowEntriesCount++;
        rRecord.entries(line);
      }
      if(line.equals("**")){
        inColumns = false;
      }
      if(inColumns == true){
        columnCounter++;
        rTable.setAttributes(line);
      }
    }
    this.tables.add(rTable);
  }




  //for creating records at runtime
  private void createRecord(){
    Scanner keyboard = new Scanner(System.in);
    String user_table_name;
    Record newRecord = new Record();
    String target = "no_target";
    System.out.println("Where should this record go?");
    target = keyboard.nextLine();
    //change all this to a dialogue function
    System.out.println("Enter record entries. Please make");
    System.out.println("sure you have the right number of");
    System.out.println("colums for the target table, an ");
    System.out.println("additional first entry will be ");
    System.out.println("added to act as a unique row ID");
    String inp = "newString";
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        newRecord.addEntry(String.valueOf(t.generateRecordKey()));
      }
    }
    while(!inp.equals("**")){
      inp = keyboard.nextLine();
      if(!inp.equals("**")){
        newRecord.entries(inp);
      }
    }
    System.out.println("new record is:");
    newRecord.printEntries();
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target) &&
      t.getNumberOfColumns() == newRecord.getNUmberOfEntries()){
        t.insertRecord(newRecord);
      }
    }
  }

  //user options at runtime
  private String control(){
    System.out.println("What would you like to do?");
    Scanner keyboard = new Scanner(System.in);
    String instruction = keyboard.nextLine();

    if(instruction.equals("create table")){
      createTable();
    }
    if(instruction.equals("create record")){
      createRecord();
    }
    if(instruction.equals("show tables")){
      printTables();
    }
    if(instruction.equals("list tables")){
      printTableNames();
    }
    //delete record, change record, delete table
    if(instruction.equals("change record")){
      alterRecord();
    }
    if(instruction.equals("save tables")){
      for(Table t : this.tables){
        t.saveTable();
      }
    }
    if(instruction.equals("add column")){
      addCoulmn();
    }
    if(instruction.equals("remove column")){
      removeCoulmn();
    }
    if(instruction.equals("find all")){
      findEntries();
    }
    if(instruction.equals("show column")){
      showColumn();
    }
    if(instruction.equals("delete saved")){// fix this!!!!
      deleteSavedTable();
    }
    if(instruction.equals("move record")){// fix this!!!!
      recordTransfer();
    }
    return instruction;
  }
  private void recordTransfer(){
    System.out.println("Which table is the record in?");
    Scanner keyboard = new Scanner(System.in);
    String current = keyboard.nextLine();
    System.out.println("Which record ID?");
    String recID = keyboard.nextLine();
    System.out.println("Where would you like to move it?");
    String target = keyboard.nextLine();
    Record recMove = new Record();
    Table targetTable = new Table("temp1");
    Table currentTable = new Table("temp2");
    boolean foundCurrent = false;
    boolean foundTarget = false;
    for(Table t : this.tables){
      if(t.getinstanceName().equals(current)){
        currentTable = t;
        foundCurrent = true;
        recMove = t.getTableRecord(recID);
      }
      if(t.getinstanceName().equals(target)){
        targetTable = t;
        foundTarget = true;
      }
    }
    if(foundCurrent == true && foundTarget == true
    && ! (recMove.getEntry(0).equals("-1"))
    && recMove.getNUmberOfEntries() ==
    targetTable.getNumberOfColumns()){
      moveRecord(currentTable, targetTable, recMove);
    }
    else{
      System.out.println("invalid");
    }
  }
  private void moveRecord(Table current, Table target, Record rowToInsert){
    Record temp = new Record();
    Record transfer = new Record();
    transfer.entries(rowToInsert.getEntriesArray());
    transfer.printEntries();
    transfer.setEntry(0, String.valueOf(target.generateRecordKey()));
    target.insertRecord(transfer);
    transfer.printEntries();
  }

  private void alterRecord(){
    System.out.println("Which table is the record in?");
    Scanner keyboard = new Scanner(System.in);
    String target = keyboard.nextLine();
    System.out.println("What is the ID of the record?");
    String rowToChange = keyboard.nextLine();
    System.out.println("Which number column(ID is 0)?");
    int columnNo = Integer.parseInt(keyboard.nextLine());
    System.out.println("what would you like to replace it with?");
    String newData = keyboard.nextLine();
    Table targetTable = new Table("temp1");
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        targetTable = t;
      }
    }
    //check column and row exist
    if(targetTable.getNumberOfColumns() >= columnNo){
      for(Record r : targetTable.getAllRecords()){
        if(r.getKey().equals(rowToChange)){
          targetTable.changeRecord(rowToChange, columnNo, newData);
        }
      }
    }
  }
  private void addCoulmn(){
    System.out.println("To which table?");
    Scanner keyboard = new Scanner(System.in);
    String target = keyboard.nextLine();
    System.out.println("What is the name of the column?");
    String newColumn = keyboard.nextLine();
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        t.setAttributes(newColumn);
      }
    }
  }
  private void showColumn(){
    System.out.println("From which table?");
    Scanner keyboard = new Scanner(System.in);
    String target = keyboard.nextLine();
    System.out.println("What is the name of the column?");
    String column = keyboard.nextLine();
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        t.printColumn(column);
      }
    }
  }

  private void findEntries(){
    System.out.println("Which table?");
    Scanner keyboard = new Scanner(System.in);
    String target = keyboard.nextLine();
    System.out.println("Which column?");
    String column = keyboard.nextLine();
    System.out.println("What would you like to search for?");
    String data = keyboard.nextLine();
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        t.subTable(column, data);
      }
    }
  }

  private void removeCoulmn(){
    System.out.println("From which table?");
    Scanner keyboard = new Scanner(System.in);
    String target = keyboard.nextLine();
    System.out.println("What is the name of the column?");
    String newColumn = keyboard.nextLine();
    for(Table t : this.tables){
      if(t.getinstanceName().equals(target)){
        t.deleteAttribute(newColumn);
      }
    }
  }

  //for creating tables at runtime
  private void createTable(){
    System.out.println("Input a table name to create a table");
    Scanner keyboard = new Scanner(System.in);
    String user_table_name;
    Table newTable;
    user_table_name = keyboard.nextLine();
    newTable = new Table(user_table_name);
    this.tables.add(newTable);
    newTable.setAttributes("ID");
    System.out.println("Enter column names");
    String inp = "newString";
    while(!inp.equals("**")){
      inp = keyboard.nextLine();
      if(!inp.equals("**")){
        newTable.setAttributes(inp);
      }
    }
  }

  //deletes a table from directory
  //and removes from database tables
  private void deleteSavedTable(){
    System.out.println("Input a table name to delete a table");
    Scanner keyboard = new Scanner(System.in);
    String tableName = keyboard.nextLine();
    String search = new StringBuilder().append("TableSave_").append(tableName).toString();
    System.out.println(tableName);
    Table temp = new Table("temporary");
    for(File f : getLocalFiles()){
      System.out.println("file search: " + f.toString());
      //if(f.toString().contains(search)){//&& file is a legit save
      if(f.getName().equals(search)){
        System.out.println("file found: " + f);
        f.delete();
        for(Table t : this.tables){
          if(t.getinstanceName().equals(tableName)){
            System.out.println("again:" + tableName);
            temp = t;
          }
        }
      }
    }
    this.tables.remove(temp);
  }

  private void exitDialogue(){
    System.out.println("Would you like to save first, y/n?");
    Scanner keyboard = new Scanner(System.in);
    String save = keyboard.nextLine();
    if(save.equals("y")){
      for(Table t : this.tables){
        t.saveTable();
      }
    }
  }

  private void run() throws IOException {
    this.tables = new ArrayList<Table>();
    loadLocalFiles();
    while(!(control().equals("*exit*"))){
    }
    exitDialogue();
  }

  public static void main(String[] args) throws IOException {
    Db program = new Db();
    program.run();
  }
}
