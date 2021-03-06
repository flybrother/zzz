
package com.aeviou.back;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import com.aeviou.Util.Constants;
import com.aeviou.Util.Globals;

public class PinyinTree {
    private class PinyinType{
        char[] sheng;
        char[] yun;
        PinyinTreeNode node;

        @Override
        public String toString(){
            StringBuffer sb = new StringBuffer();
            if (sheng[0] != '\''){
                sb.append(sheng);
            }
            if (yun[0] != '\''){
                sb.append(yun);
            }
            return sb.toString();
        }
    }

    private class PinyinTreeNode{
        byte letter;
        PinyinTreeNode[] children;
        int childrenSize;
        char id;
        char[] possibleId;
        
        public PinyinTreeNode() {
        	this.id = (char)-1;
            this.letter = (byte)0;
            this.childrenSize = 0;
        }
    }
    
    public static final int MOHU_SHENG_Z_ZH = 0x0001;
    public static final int MOHU_SHENG_C_CH = 0x0002;
    public static final int MOHU_SHENG_S_SH = 0x0004;
    public static final int MOHU_SHENG_L_N = 0x0008;
    public static final int MOHU_SHENG_F_H = 0x0010;
    public static final int MOHU_SHENG_R_L = 0x0020;

    public static final int MOHU_YUN_AN_ANG = 0x0001;
    public static final int MOHU_YUN_EN_ENG = 0x0002;
    public static final int MOHU_YUN_IN_ING = 0x0004;
    public static final int MOHU_YUN_IAN_IANG = 0x0008;
    public static final int MOHU_YUN_UAN_UANG = 0x0010;
    
    private static final int PINYIN_COUNT = 405;
    private static PinyinTree instance = null;

    private PinyinType[] pinyins = null;
    private char[] returnArray;
    private PinyinTreeNode[] roots;
    private int moHuShengFlag;
    private int moHuYunFlag;
    private String lastPinyin;
    private PinyinTreeNode lastNode;

    public static PinyinTree getInstance(){
        if (instance == null){
            instance = new PinyinTree();
        }
        return instance;
    }

    private PinyinTree(){
        pinyins = new PinyinType[PINYIN_COUNT];
        roots = new PinyinTreeNode[26];
        for (int i = 0; i < 26; i++) {
        	roots[i] = new PinyinTreeNode();
        	roots[i].letter = (byte)('a'+i);
        }
        returnArray = new char[6];
        
        String[] line;
        char count = 0;
		try {
			Scanner pinyinFile;
			pinyinFile = new Scanner(new InputStreamReader(
					Globals.service.getResources().getAssets().open(Constants.PINYIN_LIST_FILE)));
			while (pinyinFile.hasNext()){
	            line = pinyinFile.nextLine().split(" ");
	            addPinyin(line[0], line[1], line[2], count);
	            count++;
	        }
	        pinyinFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setMoHuFlag(0, 0);
    }

    private void addPinyin(String pinyin, String sheng, String yun, char id){
        char c = pinyin.charAt(0);
        // pinyin without sheng'mu
        if (c == '\''){
            c = 'v';
        }
        int index = c - 'a';

        PinyinTreeNode node = roots[index];
        for (int i = 1; i < pinyin.length(); i++){
            c = pinyin.charAt(i);
            if (node.children == null){
                node.children = new PinyinTreeNode[6];
            }
            if (node.childrenSize != 0){
                if (node.children[node.childrenSize - 1].letter == c){
                    node = node.children[node.childrenSize - 1];
                    continue;
                }
            }
            node.children[node.childrenSize] = new PinyinTreeNode();
            node.childrenSize++;
            node = node.children[node.childrenSize - 1];
            node.letter = (byte)c;
        }
        node.id = id;

        pinyins[id] = new PinyinType();
        pinyins[id].sheng = sheng.toCharArray();
        pinyins[id].yun = yun.toCharArray();
        pinyins[id].node = node;
    }

    private PinyinTreeNode getLastNode(String pinyin){
        if (pinyin == null){
            return null;
        }

        if (pinyin.length() == 0){
            return null;
        }

        if (pinyin.equals(lastPinyin)){
            return lastNode;
        }

        byte c = (byte)pinyin.charAt(0);
        if (c == '\''){
            c = 'v';
        }
        
        int index = c - 'a';
        PinyinTreeNode node = null;
        if (index < 0 || index >= roots.length){
        	// possible?
        	return null;
        }else{
        	node = roots[index];
            if (node == null){
                return null;
            }
        }

        for (int i = 1; i < pinyin.length(); i++){
            c = (byte)pinyin.charAt(i);
            if (node.children == null){
                return null;
            }
            node = biSearchChild(node, c);
        }
        lastPinyin = pinyin;
        lastNode = node;
        return node;
    }
    
    // binary search children c of node root
    public PinyinTreeNode biSearchChild(PinyinTreeNode root, byte c) {
    	int a = 0;
        int b = root.childrenSize - 1;
        int m;
        while (a <= b){
            m = (a + b) / 2;
            if (root.children[m].letter < c){
                a = m + 1;
            }else if(root.children[m].letter > c){
                b = m - 1;
            }else{
                root = root.children[m];
                return root;
            }
        }
        return null;
    }

    // get id of pinyin
    public char getPinyinId(String pinyin){
        PinyinTreeNode node = getLastNode(pinyin);
        if (node == null){
            return (char)-1;
        }
        return node.id;
    }

    public String getPinyinString(char id){
        if (id < 0 || id >= pinyins.length){
            return "error in get pinyin";
        }
        return pinyins[id].toString();
    }

    public char[] getPinyinPossibleId(String pinyin){
        PinyinTreeNode node = getLastNode(pinyin);
        if (node == null){
            return null;
        }
        return node.possibleId;
    }

    public char[] getNextLetters(String pinyin){
        PinyinTreeNode node = getLastNode(pinyin);
        for (int i = 0; i < 6; i++){
            returnArray[i] = (char)-1;
        }
        if (node == null){
            return returnArray;
        }
        if (node.children != null){
            for (int i = 0; i < 6; i++){
                if (node.children[i] != null){
                    returnArray[i] = (char)node.children[i].letter;
                }
            }
        }
        
        
        return returnArray;
    }

    public boolean isCompletedPinyin(String pinyin){
        return getPinyinId(pinyin) != (char)-1;
    }

    public void setMoHuFlag(int moHuShengFlag, int moHuYunFlag){
        this.moHuShengFlag = moHuShengFlag;
        this.moHuYunFlag = moHuYunFlag;
        updatePossibleIds();
        System.gc();
    }
    
    public ArrayList<Character> getSyllableIdsStartWith(String prefix) {
    	ArrayList<Character> result = new ArrayList<Character>();
    	PinyinTreeNode node = roots[prefix.charAt(0) - 'a'];;
    	// consume prefix
    	for (int i = 1; i < prefix.length(); i++)
    		node = biSearchChild(node, (byte)prefix.charAt(i));
    	// add all leaves of subtree 'node' into result
    	Queue<PinyinTreeNode> traverseQueue = new LinkedList<PinyinTreeNode>();
    	traverseQueue.add(node);
    	while (traverseQueue.isEmpty() == false) {
    		node = traverseQueue.poll();
    		// extract all children
    		for (int i = 0; i < node.childrenSize; i++)
    			traverseQueue.offer(node.children[i]);
    		
    		if (node.id != (char)-1) {
    			result.add(node.id);
//    			System.out.println((int)(node.id) + getPinyinString(node.id));
    		}
    	}
    	
    	return result;
    }

    private void updatePossibleIds(){
        String[] possibleShengs = new String[3];
        int possibleShengsSize = 0;
        String[] possibleYuns = new String[2];
        int possibleYunsSize = 0;
        char[] possibleIds = new char[6];
        int possibleIdsSize = 0;

        for (int i = 0; i < PINYIN_COUNT; i++){
        	if (pinyins[i] == null)
        		continue;
        	
            possibleShengsSize = 0;
            possibleYunsSize = 0;
            possibleIdsSize = 0;

            String sheng = new String(pinyins[i].sheng);
            possibleShengs[possibleShengsSize] = sheng;
            possibleShengsSize++;
            if ((moHuShengFlag & MOHU_SHENG_Z_ZH) != 0){
                if (sheng.equals("z")){
                    possibleShengs[possibleShengsSize] = "zh";
                    possibleShengsSize++;
                }else if(sheng.equals("zh")){
                    possibleShengs[possibleShengsSize] = "z";
                    possibleShengsSize++;
                }
            }
            if ((moHuShengFlag & MOHU_SHENG_C_CH) != 0){
                if (sheng.equals("c")){
                   possibleShengs[possibleShengsSize] = "ch";
                   possibleShengsSize++;
                }else if(sheng.equals("ch")){
                   possibleShengs[possibleShengsSize]="c";
                   possibleShengsSize++;
                }
            }
            if ((moHuShengFlag & MOHU_SHENG_S_SH) != 0){
                if (sheng.equals("s")){
                   possibleShengs[possibleShengsSize] = "sh";
                   possibleShengsSize++;
                }else if(sheng.equals("sh")){
                   possibleShengs[possibleShengsSize] = "s";
                   possibleShengsSize++;
                }
            }
            if ((moHuShengFlag & MOHU_SHENG_L_N) != 0){
                if (sheng.equals("l")){
                   possibleShengs[possibleShengsSize] = "n";
                   possibleShengsSize++;
                }else if(sheng.equals("n")){
                   possibleShengs[possibleShengsSize] = "l";
                   possibleShengsSize++;
                }
            }
            if ((moHuShengFlag & MOHU_SHENG_F_H) != 0){
                if (sheng.equals("f")){
                   possibleShengs[possibleShengsSize] = "h";
                   possibleShengsSize++;
                }else if(sheng.equals("h")){
                   possibleShengs[possibleShengsSize] = "f";
                   possibleShengsSize++;
                }
            }
            if ((moHuShengFlag & MOHU_SHENG_R_L) != 0){
                if (sheng.equals("r")){
                   possibleShengs[possibleShengsSize] = "l";
                   possibleShengsSize++;
                }else if(sheng.equals("l")){
                   possibleShengs[possibleShengsSize] = "r";
                   possibleShengsSize++;
                }
            }

            String yun = new String(pinyins[i].yun);
            possibleYuns[possibleYunsSize] = yun;
            possibleYunsSize++;
            if ((moHuYunFlag & MOHU_YUN_AN_ANG) != 0){
                if (yun.equals("an")){
                    possibleYuns[possibleYunsSize] = "ang";
                    possibleYunsSize++;
                }else if(yun.equals("ang")){
                    possibleYuns[possibleYunsSize] = "an";
                    possibleYunsSize++;
                }
            }
            if ((moHuYunFlag & MOHU_YUN_EN_ENG) != 0){
                if (yun.equals("en")){
                    possibleYuns[possibleYunsSize] = "eng";
                    possibleYunsSize++;
                }else if(yun.equals("eng")){
                    possibleYuns[possibleYunsSize] = "en";
                    possibleYunsSize++;
                }
            }
            if ((moHuYunFlag & MOHU_YUN_IN_ING) != 0){
                if (yun.equals("in")){
                    possibleYuns[possibleYunsSize] = "ing";
                    possibleYunsSize++;
                }else if(yun.equals("ing")){
                    possibleYuns[possibleYunsSize] = "in";
                    possibleYunsSize++;
                }
            }
            if ((moHuYunFlag & MOHU_YUN_IAN_IANG) != 0){
                if (yun.equals("ian")){
                    possibleYuns[possibleYunsSize] = "iang";
                    possibleYunsSize++;
                }else if(yun.equals("iang")){
                    possibleYuns[possibleYunsSize] = "ian";
                    possibleYunsSize++;
                }
            }
            if ((moHuYunFlag & MOHU_YUN_UAN_UANG) != 0){
                if (yun.equals("uan")){
                    possibleYuns[possibleYunsSize] = "uang";
                    possibleYunsSize++;
                }else if(yun.equals("uang")){
                    possibleYuns[possibleYunsSize] = "uan";
                    possibleYunsSize++;
                }
            }

            if (possibleShengsSize == 1 && possibleYunsSize == 1){
                pinyins[i].node.possibleId = new char[1];
                pinyins[i].node.possibleId[0] = pinyins[i].node.id;
            }else{
                char pinyinId;
                for (int a = 0; a < possibleShengsSize; a++){
                    for (int b = 0; b < possibleYunsSize; b++){
                        pinyinId = getPinyinId(possibleShengs[a] + possibleYuns[b]);
                        if (pinyinId != (char)-1){
                            possibleIds[possibleIdsSize] = pinyinId;
                            possibleIdsSize++;
                        }
                    }
                }
                pinyins[i].node.possibleId = new char[possibleIdsSize];
                for (int j = 0; j < possibleIdsSize; j++){
                    pinyins[i].node.possibleId[j] = possibleIds[j];
                }
            }
        }
    }
}
