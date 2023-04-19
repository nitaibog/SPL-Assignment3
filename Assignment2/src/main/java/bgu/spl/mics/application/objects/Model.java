package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public void setStudent(Student student) {
        this.student = student;
    }

    public enum status{
        PreTrained, Training, Trained, Tested
    }

   public enum results{
        None, Good, Bad
    }

    private final String name;
    private Data data;
    private Student student;  // we can remove it;
    private status currStatus;
    private results currResult;
    private Data.Type type;
    private int size;


    public Model(String name,Student stud, Data.Type type,int size){
        this.name = name;
        this.student = stud;
        this.type = type;
        this.size = size;
    }

    public void setStatus(status stat) {
        this.currStatus = stat;
    }

    public void setResult(results res) {
        this.currResult = res;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public status getStatus() {
        return currStatus;
    }

    public results getResult() {
        return currResult;
    }

    public Student getStudent() {
        return student;
    }


    public void startModel(){
        this.data = new Data(type, size);
        this.currResult = results.None;
        this.currStatus = status.PreTrained;
    }
}
