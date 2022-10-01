package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private double[] nums;
    private String[] opps;
    private String currentNum;
    int numsIndex;
    boolean lastIsNum;
    boolean holdNeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nums = new double[100];
        numsIndex = 0;
        opps = new String[100];
        lastIsNum = false;
        holdNeg = false;
        currentNum = "";
    }

    /**
     * Appends the button's text to resultEdit's text
     * @param view  is a button
     */
    public void makeStr(View view) {
        EditText eText = (EditText) findViewById(R.id.resultEdit);
        Button btn = (Button) findViewById(view.getId());
        eText.setText(eText.getText() + "" + btn.getText());
    }

    /**
     * Sets the current nums[numIndex] based on holdNeg
     * the value is negative if holdneg if true
     */
    private void setNum(){
        nums[numsIndex] = holdNeg ? -Double.parseDouble(currentNum) : Double.parseDouble(currentNum);
        holdNeg = false;
    }

    /**
     * adds a number or decimal to the equation
     * @param view  is the numeric button clicked
     */
    public void onClickNumOpp(View view) {
        String num = (String)((Button)findViewById(view.getId())).getText();
        if(num.contains(".") && currentNum.contains(".")){      //numbers don't have multiple decimals
            return;
        }
        currentNum = currentNum + num;
        lastIsNum = true;
        makeStr(view);
    }

    /**
     * adds an operation to the equation
     * @param view  is the operation button clicked
     */
    public void onClickMathOpp(View view) {
        String opp = (String)((Button)findViewById(view.getId())).getText();
        if(!lastIsNum){
            if(opp.contains("-")){          //if the previous entry was not a number, this will toggle between negative and positive
                holdNeg = !holdNeg;
                makeStr(view);
                return;
            }
            opps[numsIndex] = (String)((Button)findViewById(view.getId())).getText();   //replace the previous opperator with this one
            return;
        }
        setNum();
        opps[numsIndex] = (String)((Button)findViewById(view.getId())).getText();
        numsIndex++;
        currentNum = "";
        lastIsNum = false;
        makeStr(view);
        printEq();
    }

    /**
     * Assigns values to opps[i], opps[i-1], nums[i] so that:      0 opps[i-1] nums[i] opps[i] nums[i+1] = opps[i-1] nums[i+1]
     * @param i is the current index of opps[] in a loop
     */
    private void setPreviousMulNum(int i){
        if(i >= 1){
            opps[i] = opps[i-1];
            nums[i] = 0;
        } else{
            opps[i] = "+";
            nums[i] = 0;
        }
        printEq();
    }

    /**
     * converts opps[] with nums[] so that multiplication and division becomes addition and subtraction
     */
    private void numOppsMulPass1(){
        for(int i = 0; i<numsIndex; i++){
            if(opps[i].contains("*")){
                nums[i+1] = nums[i] * nums[i+1];
                setPreviousMulNum(i);
            }else if(opps[i].contains("/")){
                nums[i+1] = nums[i] / nums[i+1];
                setPreviousMulNum(i);
            }
        }
    }

    /**
     * prints nums[] with opps[] in a human-readable format
     */
    public void printEq(){
        System.out.println("\nEQ:");
        for(int i = 0;i<numsIndex;i++){
            System.out.print(nums[i]+""+opps[i]);
        }
        System.out.print(nums[numsIndex+1]+"\n");
    }

    /**
     * adds nums[] using opps[]
     * @return The final answer
     */
    private double numOppsMulPass2(){

        System.out.println("\n\nStart Pass2");
        for(int i = 0; i<numsIndex; i++){
            if(opps[i].contains("+")){
                nums[i+1] = nums[i] + nums[i+1];
            }else{
                nums[i+1] = nums[i] - nums[i+1];
            }
        }
        return nums[numsIndex];
    }

    /**
     * generates the answer
     * @param view is buttonEqu
     */
    public void onClickMathEqu(View view) {
        if(!lastIsNum){
            if(numsIndex-1 >= 0){
                opps[numsIndex-1] = null;
            } else {
                return;
            }
        }
        setNum();
        printEq();
        numOppsMulPass1();
        printEq();
        EditText eText = (EditText) findViewById(R.id.resultEdit);
        eText.setText(Double.toString(numOppsMulPass2()));

        //reset private vars
        currentNum = eText.getText().toString();
        numsIndex = 0;
        lastIsNum = true;
    }

    /**
     * clears the equation
     * @param view is buttonCE
     */
    public void onClickCE(View view){
        currentNum = "0";
        numsIndex = 0;
        lastIsNum = false;
        ((EditText) findViewById(R.id.resultEdit)).setText("");
    }
}