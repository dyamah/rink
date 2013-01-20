package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;


public class BinaryTrie implements Serializable {

    private static final long serialVersionUID = -3900615802793591028L;

    static class Node implements Serializable {

        private static final long serialVersionUID = -6262739348551415510L;

        private int id_ ;
        private int left_ ;
        private int right_ ;
        private double value_ ;
        private boolean terminal_ ;
        Node(){
            id_    =  0;
            left_  =  0;
            right_ =  0;
            value_ =  0;
            terminal_ = false;
        }

        private void terminate(){
            terminal_ = true;
        }
    }

    private Node[] nodes_ ;
    private int size_ ;


    public BinaryTrie(){
        nodes_ = new Node[100];
        for(int i = 0; i < nodes_.length; i++)
            nodes_[i] = new Node();
        size_ = 1;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        size_ = in.readInt();
        nodes_ = new Node[size_];
        for(int i = 0 ; i < size_; i++)
            nodes_[i] = (Node) in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeInt(size_);
        for(int i = 0 ; i < size_; i++)
            out.writeObject(nodes_[i]);
    }


    private void resize(int m){
        if (nodes_.length < size_ + m){
            nodes_ = Arrays.copyOf(nodes_, size_ + m);
            for(int i = size_; i < nodes_.length; i++)
                nodes_[i] = new Node();
        }
    }

    public int set(int[] sequence, double value){
        int t = 0;
        int i = 0;
        Node n = null;
        for(; i < sequence.length;){
            n = nodes_[t];
            if (n.id_ == sequence[i]){
                i++;
                if (n.left_ <= t)
                    break ;
                if ( i == sequence.length)
                    continue;

                t = n.left_ ;
            } else {
                if (n.right_ <= t){
                    break ;
                }
                t = n.right_;
            }
        }
        if (n == null)
            return -1;

        // matched
        if (i == sequence.length){
            n.terminate();
            n.value_ = value;
            return t;
        }

        resize(sequence.length);

        for(; i < sequence.length; i++){
            if (i > 0 && n.id_ == sequence[i-1]){
                n.left_ = size_ ;
            } else {
                n.right_ = size_ ;
            }
            n = nodes_[size_++] ;
            n.id_ = sequence[i];
        }
        n.terminate();
        n.value_ = value;
        return size_-1;
    }

    public int update(int[] sequence, double value){
        int t = 0;
        int i = 0;
        Node n = null;
        for(; i < sequence.length;){
            n = nodes_[t];
            if (n.id_ == sequence[i]){
                i++;
                if (n.left_ <= t)
                    break ;
                if ( i == sequence.length)
                    continue;

                t = n.left_ ;
            } else {
                if (n.right_ <= t){
                    break ;
                }
                t = n.right_;
            }
        }
        if (n == null)
            return -1;

        // matched
        if (i == sequence.length){
            n.terminate();
            n.value_ += value;
            return t;
        }

        resize(sequence.length);

        for(; i < sequence.length; i++){
            if (i > 0 && n.id_ == sequence[i-1]){
                n.left_ = size_ ;
            } else {
                n.right_ = size_ ;
            }
            n = nodes_[size_++] ;
            n.id_ = sequence[i];
        }
        n.terminate();
        n.value_ += value;
        return size_-1;
    }

    public double get(int[] sequence){
        int t = 0;
        int i = 0;
        Node n = null;
        for(; i < sequence.length;){
            n = nodes_[t];
            if (n.id_ == sequence[i]){
                i++;
                if (n.left_ <= t)
                    break ;

                t = n.left_ ;
            } else {
                if (n.right_ <= t){
                    return 0;
                }
                t = n.right_;
            }
        }
        if (n == null)
            return 0;
        if (i == sequence.length && n.terminal_)
            return n.value_;

        return 0 ;
    }

}
