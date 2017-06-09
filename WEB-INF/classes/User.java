public class User{
  private String username;
  private int password;
  private int id;

  public User(String u, int p){
    username = u;
    password = p;
    id = generateId(username, password);
  }

  //Create user ID  
  private int generateId(String str, int num){
    String concat = str + num;
    return concat.hashCode();
  }

  public String getUsername(){
    return username;
  }

  public int getId(){
    return id;
  }
}
