package com.example.uvproyect;

import android.widget.ImageView;

import java.util.HashMap;

public class DisplayManager {
    ImageView[] displaySlots;
    HashMap<Character, Integer> diccionary;
    DisplayManager(ImageView[] displaySlots){
        this.displaySlots = displaySlots;
        diccionary = new HashMap<>();
        for (ImageView i: displaySlots) {
            i.setImageResource(R.drawable.dnothing);
        }
        initDiccionary();
    }

    public void setTextOnDisplay(String phrase){
        phrase = phrase.toLowerCase();
        if(phrase.length()>20)
            return;
        int i;
        for (i = 0; i < phrase.length(); i++) {
            if((!diccionary.containsKey(phrase.charAt(i)))) {
                displaySlots[i].setImageResource(R.drawable.dnothing);
                continue;
            }
            displaySlots[i].setImageResource(diccionary.get(phrase.charAt(i)));
        }
        for (int j = i; j < 20; j++)
            displaySlots[j].setImageResource(R.drawable.dnothing);
    }

    private void initDiccionary() {
        diccionary.put('a', R.drawable.a);
        diccionary.put('b', R.drawable.b);
        diccionary.put('c', R.drawable.c);
        diccionary.put('d', R.drawable.d);
        diccionary.put('e', R.drawable.e);
        diccionary.put('f', R.drawable.f);
        diccionary.put('g', R.drawable.g);
        diccionary.put('h', R.drawable.h);
        diccionary.put('i', R.drawable.i);
        diccionary.put('j', R.drawable.j);
        diccionary.put('k', R.drawable.k);
        diccionary.put('l', R.drawable.l);
        diccionary.put('m', R.drawable.m);
        diccionary.put('n', R.drawable.n);
        diccionary.put('o', R.drawable.o);
        diccionary.put('p', R.drawable.p);
        diccionary.put('q', R.drawable.q);
        diccionary.put('r', R.drawable.r);
        diccionary.put('s', R.drawable.s);
        diccionary.put('t', R.drawable.t);
        diccionary.put('u', R.drawable.u);
        diccionary.put('v', R.drawable.v);
        diccionary.put('w', R.drawable.w);
        diccionary.put('x', R.drawable.x);
        diccionary.put('y', R.drawable.y);
        diccionary.put('z', R.drawable.z);
        diccionary.put('1', R.drawable.d1);
        diccionary.put('2', R.drawable.d2);
        diccionary.put('3', R.drawable.d3);
        diccionary.put('4', R.drawable.d4);
        diccionary.put('5', R.drawable.d5);
        diccionary.put('6', R.drawable.d6);
        diccionary.put('7', R.drawable.d7);
        diccionary.put('8', R.drawable.d8);
        diccionary.put('9', R.drawable.d9);
        diccionary.put('0', R.drawable.d0);
    }
}
