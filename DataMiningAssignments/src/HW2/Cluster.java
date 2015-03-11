package HW2;

import java.util.ArrayList;

public class Cluster {

    Representative value;
    Cluster leftChild;
    Cluster rightChild;
    int index;

    public Cluster(Representative value,Cluster leftChild,Cluster rightChild){
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        index = -1;
    }

    public Cluster(Representative value,int index){
        this.value = value;
        this.index = index;
        this.leftChild = null;
        this.rightChild = null;
    }


}

