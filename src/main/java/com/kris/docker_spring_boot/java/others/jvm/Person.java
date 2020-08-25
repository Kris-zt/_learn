package com.kris.docker_spring_boot.java.others.jvm;

/**
 * Create by YSOcean
 */
public class Person {

    private Byte[] _1MB = null;

    public Person() {
        /*
         * 这个成员属性的作用纯粹就是占据一定内存，以便在日志中查看是否被回收
         */
        _1MB = new Byte[1 * 1024 * 1024];
    }

    private Person father;

    private Person son;

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getSon() {
        return son;
    }

    public void setSon(Person son) {
        this.son = son;
    }

    public static void main(String[] args) {

        Person father = new Person();
        Person son = new Person();
        father.setSon(son);
        son.setFather(father);

        father = null;
        son = null;

        /*
         * 调用此方法表示希望进行一次垃圾回收。但是它不能保证垃圾回收一定会进行，
         * 而且具体什么时候进行是取决于具体的虚拟机的，不同的虚拟机有不同的对策。
         */
        System.gc();
    }
}
