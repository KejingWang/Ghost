/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;


public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    private int binarySearch(String prefix) {
        //binary search to get all candidates and do picking
        int l = 0, r = words.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            String word = words.get(m);
            int len = Math.min(prefix.length(), word.length());
            if (prefix.compareTo(word.substring(0, len)) == 0) {
                return m;
            } else if (prefix.compareTo(word.substring(0, len)) > 0) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        return -1;
    }


    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.equals("")) { //if ghostText is empty, return a random word
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        }

        int resultIndex = binarySearch(prefix); //the index of the word found
        if (resultIndex == -1) return null;
        return words.get(resultIndex);

    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        Random random = new Random();

        if (prefix.equals("")) { //if ghostText is empty, return a random word
            return words.get(random.nextInt(words.size()));
        }

        int resultIndex = binarySearch(prefix); //the index of the word found
        if (resultIndex == -1) return null;

        ArrayList<String> oddLen = new ArrayList<>();
        ArrayList<String> evenLen = new ArrayList<>();
        String word = words.get(resultIndex);
        for (int i = resultIndex; i >= 0; i--) {
            System.out.println("i" + i);
            if (word.equals(words.get(i))) {
                if (word.length() % 2 == 0) evenLen.add(words.get(i));
                if (word.length() % 2 != 0) oddLen.add(words.get(i));
            } else {
                break;
            }
        }
        for (int i = resultIndex + 1; i < words.size(); i++) {
            if (word.equals(words.get(i))) {
                if (word.length() % 2 == 0) evenLen.add(words.get(i));
                if (word.length() % 2 != 0) oddLen.add(words.get(i));
            } else {
                break;
            }
        }
        System.out.println(oddLen.size() + ", " + evenLen.size());

        for (int i = 0; i < oddLen.size(); i++) {
            System.out.println(oddLen.get(i));
        }
        for (int i = 0; i < evenLen.size(); i++) {
            System.out.println(evenLen.get(i));
        }

        //make sure wordlen - prefixlen = even, as comp goes first
        if (prefix.length() % 2 == 0) { //prefix has even len, comp pick from evenLen
            if (evenLen.size() != 0) {
                return evenLen.get(random.nextInt(evenLen.size()));
            } else {
                return oddLen.get(random.nextInt(oddLen.size()));
            }
        } else { //prefix has odd len, comp picks from oddLen
            if (oddLen.size() != 0) {
                return oddLen.get(random.nextInt(oddLen.size()));
            } else {
                return evenLen.get(random.nextInt(evenLen.size()));
            }
        }

        //return selected;
    }
}
