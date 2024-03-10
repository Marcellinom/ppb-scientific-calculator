package com.example.calculator;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.entry);
        input.setKeyListener(null);

        Resources r = getResources();
        String name = getPackageName();
        for (int i = 0; i <= 9; i++) {
            
            // ini bikin lemot tapi dari pada registerin satu satu :\
            Button b = (Button)findViewById(r.getIdentifier("num_"+i, "id", name)); 
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText input = findViewById(R.id.entry);
                    input.setText(input.getText().toString().concat(((Button)v).getText().toString()));
                }
            });
        }
        String[] symbol = {"add", "divide", "substract", "multiply", "comma"};
        for (String symb: symbol) {
            
            // ini bikin lemot tapi dari pada registerin satu satu :\
            Button b = (Button)findViewById(r.getIdentifier("symbol_"+symb, "id", name)); 
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText input = findViewById(R.id.entry);
                    String[] symbols = {"+", ":", "-", "x", "."};
                    String current_input = input.getText().toString();
                    if(!current_input.isEmpty() && Arrays.asList(symbols).contains(current_input.substring(current_input.length() - 1))) {
                        current_input = current_input.substring(0, current_input.length() - 1);
                    }

                    input.setText(current_input.concat(((Button)v).getText().toString()));
                }
            });
        }

        Button b = findViewById(R.id.symbol_clear);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.entry);
                input.setText("");
            }
        });

        (findViewById(R.id.symbol_result)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.entry);
                double result = eval(input.getText().toString());
                if (result == (long)result) {
                    input.setText(String.format(Locale.getDefault(), "%d", (long)result));
                } else {
                    input.setText(String.format(Locale.getDefault(), "%s", result));
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static double eval(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ')
                continue;

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (operators.peek() != '(') {
                    numbers.push(calculate(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (ch == '+' || ch == '-' || ch == 'x' || ch == ':') {
                while (!operators.isEmpty() && priority(ch) <= priority(operators.peek())) {
                    numbers.push(calculate(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(calculate(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    public static int priority(char op) {
        if (op == '+' || op == '-')
            return 1;
        if (op == 'x' || op == ':')
            return 2;
        return 0;
    }

    public static double calculate(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case 'x':
                return a * b;
            case ':':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}