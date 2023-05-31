/*package com.example.mylink_10.gameRelated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<Piece> pieces;
    private List<Integer> imageVals;

    public Board() {
        pieces = new ArrayList<>();
        for(int i = 0; i < GameConf.X * GameConf.Y; i++) {
            pieces.add(new Piece(i % GameConf.X + 1, i / GameConf.X + 1));
        } // 新建piece的链表
    }

    public Piece getPiece(int x, int y) {
        if(x < 1 || x > GameConf.X || y < 1 || y > GameConf.Y) {
            return null;
        } else {
            return pieces.get((y - 1) * GameConf.X + x - 1);
        }
    } // 获取单个piece，传入二维获得一维的实际坐标

    public List<Piece> getPieces() {
        return pieces;
    } // 获取整个链表

    private void resetPos() {
        for(int i = 0; i < pieces.size(); i++) {
            pieces.get(i).setPos(i % GameConf.X + 1, i / GameConf.X + 1);
        }
    }

    public void setBoard() {
        imageVals = ImageUtil.selValues();
        ImageUtil.attchImages(pieces, imageVals); // 选图+贴图
        resetBoard(); // 打乱board
        for(Piece p: pieces) {
            p.cancelDel();
        } // 恢复已经删除的piece
    }

    public void resetBoard() {
        Collections.shuffle(pieces);
        resetPos();
        setStartPos();
    }

    public void setStartPos() { // 设置每个piece画图的初始坐标（luX,Y）
        for(Piece p: pieces) {
            p.setLuX(GameConf.START_X + (p.getX() - 1) * GameConf.PIECE_WIDTH);
            p.setLuY(GameConf.START_Y + (p.getY() - 1) * GameConf.PIECE_HEIGHT);
        }
    }
}*/

package com.example.mylink_10.gameRelated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<Piece> pieces;
    private List<Integer> imageVals;
    private int tot;
    private int sequenceId;

    public Board() {
        tot = GameConf.X * GameConf.Y;
        pieces = new ArrayList<>();
        for(int i = 0; i < GameConf.X * GameConf.Y; i++) {
            pieces.add(new Piece(i % GameConf.X + 1, i / GameConf.X + 1));
        } // 新建piece的链表
    }

    public void setBoard3() { // Board类里新加
        TempNode temp = ImageUtil.getCppRes2();
        sequenceId = temp.getId();
        imageVals = temp.getImages();
        imageVals = ImageUtil.setSolvable(imageVals);
        ImageUtil.attchImages(pieces, imageVals);
        resetPos();
        setStartPos();
    }

    public void setBoard4(int id) { // Board类里新加
        imageVals = ImageUtil.getCppRes3(id);
        imageVals = ImageUtil.setSolvable(imageVals);
        ImageUtil.attchImages(pieces, imageVals);
        resetPos();
        setStartPos();
    }

    public void setImageVals(List<Integer> imageVals) {
        this.imageVals = imageVals;
    }

    public Piece getPiece(int x, int y) {
        if(x < 1 || x > GameConf.X || y < 1 || y > GameConf.Y) {
            return null;
        } else {
            return pieces.get((y - 1) * GameConf.X + x - 1);
        }
    } // 获取单个piece，传入二维获得一维的实际坐标

    public List<Piece> getPieces() {
        return pieces;
    } // 获取整个链表

    private void resetPos() {
        for(int i = 0; i < pieces.size(); i++) {
            pieces.get(i).setPos(i % GameConf.X + 1, i / GameConf.X + 1);
        }
    }

    public void setBoard2() {
        imageVals = ImageUtil.getCppRes();
        imageVals = ImageUtil.setSolvable(imageVals);
        ImageUtil.attchImages(pieces, imageVals);
        resetPos();
        setStartPos();
    }

    public void setBoard() {
        imageVals = ImageUtil.selValues();
        ImageUtil.attchImages(pieces, imageVals); // 选图+贴图
        shuffleBoard();
    }

    public void adjTot(int num) {
        tot += num;
    }

    public void shuffleBoard() {
        Collections.shuffle(pieces);
        resetPos();
        setStartPos();
    }

    public void setStartPos() { // 设置每个piece画图的初始坐标（luX,Y）
        for(Piece p: pieces) {
            p.setLuX(GameConf.START_X + (p.getX() - 1) * GameConf.PIECE_WIDTH);
            p.setLuY(GameConf.START_Y + (p.getY() - 1) * GameConf.PIECE_HEIGHT);
        }
    }

    public int getTot() {
        return tot;
    }

    public void resetDel() {
        for(int i = 0; i < pieces.size(); i++) {
            pieces.get(i).cancelDel();
        }
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int id) {
        sequenceId = id;
    }
}

