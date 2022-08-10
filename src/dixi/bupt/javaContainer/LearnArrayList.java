package dixi.bupt.javaContainer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LearnArrayList {
    private Vector<Integer> vector = new Vector<>();
    private LinkedList<Integer> list = new LinkedList<>();
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
    private static class Student{
        private String gender;
        public Student(String gender){
            this.gender = gender;
        }
        public String getGender(){
            return gender;
        }
    }

    public static void main(String[] args){
        List<Student> list = new ArrayList<>();
        list.add(new Student("male"));
        list.add(new Student("female"));
        list.add(new Student("female"));
        list.add(new Student("male"));
        CopyOnWriteArrayList<Integer> arrayList = new CopyOnWriteArrayList<>();
        //遍历删除,除去男生
        Iterator<Student> iterator = list.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            if ("male".equals(student.getGender())) {
                iterator.remove();//使用迭代器的删除方法删除
            }
        }
    }
}
