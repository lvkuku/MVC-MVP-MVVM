package com.example.tictactoe;


import static com.example.tictactoe.R.id.action_reset;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getName();

    public enum Player {
        // 定义 井字棋选项
        X, O
    }

    public class Cell {
        private Player value;

        public Player getValue() {
            return value;
        }

        public void setValue(Player value) {
            this.value = value;
        }
    }

    private final Cell[][] cells = new Cell[3][3];
    private Player winner;
    private GameState state;
    private Player currentTurn;

    private enum GameState{
        IN_PROGRESS,FINISHED
    }

    /*
        Views
     */
    private ViewGroup buttonGrid;
    private View winnerPlayerViewGrout;
    private TextView winnerPlayerLabel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tictactoe), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonGrid = findViewById(R.id.buttonGrid);
        winnerPlayerViewGrout = findViewById(R.id.winnerPlayerViewGroup);
        winnerPlayerLabel = findViewById(R.id.winnerPlayerLabel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tictactoe,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == action_reset) {
            restart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCellClicked(View v){
        Button button = (Button) v;

        String tag = button.getTag().toString();
        int row = Integer.parseInt(tag.substring(0,1));
        int col = Integer.parseInt(tag.substring(1,2));
        Log.i(TAG, "onCellClicked: ["+ row + ","+ col + "]");

        Player playerThatMoved = mark(row,col);

        if(playerThatMoved != null){
            button.setText(playerThatMoved.toString());
            if(getWinner() != null){
                winnerPlayerLabel.setText(playerThatMoved.toString());
                winnerPlayerViewGrout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     *  开始一个新游戏 计分板和状态
     */
    public void restart(){
        clearCells();
        winner = null;
        currentTurn = Player.X;
        state = GameState.IN_PROGRESS;

        winnerPlayerViewGrout.setVisibility(View.GONE);
        winnerPlayerLabel.setText("");

        for (int i = 0; i < buttonGrid.getChildCount(); i++) {
            ((Button) buttonGrid.getChildAt(i)).setText("");
        }
    }

    /**
     *  标记当前选手选择那行那列
     *  如果不是在没有选中的9个格子里面点击将视为无效
     *  游戏结束 标记无效
     * @param row
     * @param col
     * @return
     */
    public Player mark(int row, int col) {
        Player playerThatMoved = null;

        if(isValid(row,col)){

            cells[row][col].setValue(currentTurn);
            playerThatMoved = currentTurn;

            if(isWinningMoveByPlayer(currentTurn,row,col)){
                state = GameState.FINISHED;
                winner = currentTurn;
            }else {
                //切换到另外的棋手
                flipCurrentTurn();
            }
        }
        return playerThatMoved;
    }

    public Player getWinner(){
        return winner;
    }

    private void clearCells(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private boolean isValid(int row, int col) {
        if(state == GameState.FINISHED){
            return false;
        }else if(isOutOfBounds(row) || isOutOfBounds(col)){
            return false;
        }else if(isCellValueAlreadySet(row,col)){
            return false;
        }else {
            return true;
        }

    }

    private boolean isOutOfBounds(int idx) {
        return idx < 0 || idx > 2;
    }

    private boolean isCellValueAlreadySet(int row, int col) {
        return cells[row][col].getValue() != null;
    }

    /**
     *  如果当前行 当前列 或对角线 连成一条线则为 true
     */
    private boolean isWinningMoveByPlayer(Player player, int currentRow, int currentCol) {

        // 行检查
        boolean rowWin = cells[currentRow][0].getValue() == player
                && cells[currentRow][1].getValue() == player
                && cells[currentRow][2].getValue() == player;

        // 列检查
        boolean colWin = cells[currentCol][0].getValue() == player
                && cells[currentCol][1].getValue() == player
                && cells[currentCol][2].getValue() == player;

        // 主对角线检查
        boolean diagWin = currentCol == currentRow
                && cells[0][0].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][2].getValue() == player;

        // 反对角线检查
        boolean antiDiagWin = currentCol + currentRow == 2 // currentRow + currentCol == 2 用于右上到左下
                && cells[0][2].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][0].getValue() == player;

        return rowWin || colWin || diagWin || antiDiagWin;
    }


    private void flipCurrentTurn() {
        currentTurn = currentTurn == Player.X ? Player.O : Player.X;
    }
}