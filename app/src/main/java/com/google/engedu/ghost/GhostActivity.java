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

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private SimpleDictionary simpleDictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    //
    private TextView ghostText;
    private TextView gameStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        //my code
        try{
            InputStream inputStream = assetManager.open("words.txt");
            simpleDictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        //id won't be created until oncreate
        ghostText = findViewById(R.id.ghostText);
        gameStatus = findViewById(R.id.gameStatus);
        //end
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn againString
        String text = ghostText.getText().toString();
        if(text.length()>=4 && simpleDictionary.isWord(text)){
            gameStatus.setText("Computer Wins! You formed a word!");
            return;
        }
        String word = simpleDictionary.getGoodWordStartingWith(text);
        if(word == null){
            gameStatus.setText("Computer Wins! The fragment you formed is not prefix of any word!");
            return;
        } else { //fragment is prefix of some word
            ghostText.setText(word.substring(0,text.length()+1));
        }
        userTurn = true;
        label.setText(USER_TURN);
    }

    public void challenge(View v){
        String text = ghostText.getText().toString();
        if(text.length()>=4 && simpleDictionary.isWord(text)){
            gameStatus.setText("You Win! Computer formed a word!");
            return;
        }
        String word = simpleDictionary.getGoodWordStartingWith(text);
        if(word == null){
            gameStatus.setText("You Win! The fragment is not prefix of any word!");
            return;
        } else { //fragment is prefix of some word
            gameStatus.setText("Computer Wins! The fragment is prefix of: " + word);
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //my code
        //keyCode is not ascii
        int character = event.getUnicodeChar();
        if((character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z') ){
            String currentText = ghostText.getText().toString();
            StringBuilder string = new StringBuilder(currentText);
            string.append((char)character);
            ghostText.setText(string);
            //userTurn = false;
            computerTurn();
            return true;
        }//end
        return super.onKeyUp(keyCode, event);
    }
}
