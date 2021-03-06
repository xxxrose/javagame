import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;

/*
 * Created on 2006/06/25
 */

public class MainPanel extends JPanel implements KeyListener, Runnable {
    // パネルサイズ
    public static final int WIDTH = 192;
    public static final int HEIGHT = 416;

    // フィールド
    private Field field;
    // ブロック
    private Block block;
    // 次のブロック
    private Block nextBlock;

    // ゲームループ用スレッド
    private Thread gameLoop;

    private Random rand;

    public MainPanel() {
        // パネルの推奨サイズを設定、pack()するときに必要
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // パネルがキー入力を受け付けるようにする
        setFocusable(true);

        rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        field = new Field();
        block = createBlock(field);

        addKeyListener(this);

        // ゲームループ開始
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    /**
     * ゲームループ
     */
    public void run() {
        while (true) {
            // ブロックを下方向へ移動する
            boolean isFixed = block.move(Block.DOWN);
            if (isFixed) { // ブロックが固定されたら
                // 次のブロックをランダムに作成
                nextBlock = createBlock(field);
                block = nextBlock;
            }

            // ブロックがそろった行を消す
            field.deleteLine();

            repaint();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // フィールドを描画
        field.draw(g);
        // ブロックを描画
        block.draw(g);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) { // ブロックを左へ移動
            block.move(Block.LEFT);
        } else if (key == KeyEvent.VK_RIGHT) { // ブロックを右へ移動
            block.move(Block.RIGHT);
        } else if (key == KeyEvent.VK_DOWN) { // ブロックを下へ移動
            block.move(Block.DOWN);
        } else if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) { // ブロックを回転
            block.turn();
        }

        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    /**
     * ランダムに次のブロックを作成
     * 
     * @param field
     *            フィールドへの参照
     * @return ランダムに生成されたブロック
     */
    private Block createBlock(Field field) {
        int blockNo = rand.nextInt(7); // ブロックは0-6の7種類
        switch (blockNo) {
            case Block.BAR :
                return new BarBlock(field);
            case Block.Z_SHAPE :
                return new ZShapeBlock(field);
            case Block.SQUARE :
                return new SquareBlock(field);
            case Block.L_SHAPE :
                return new LShapeBlock(field);
            case Block.REVERSE_Z_SHAPE :
                return new ReverseZShapeBlock(field);
            case Block.T_SHAPE :
                return new TShapeBlock(field);
            case Block.REVERSE_L_SHAPE :
                return new ReverseLShapeBlock(field);
        }

        return null;
    }
}
