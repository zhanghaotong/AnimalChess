import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AnimalChess {
    public static void main(String[] args) throws FileNotFoundException {
        File tile = new File("tile.txt");
        //读取地图文件
        Scanner scanner1 = new Scanner(tile);
        char[][] map = new char[7][9];
        char[][] animalData = new char[7][9];
        String dataMap = " ";
        File animal = new File("animal.txt");
        //读取动物文件
        Scanner scanner2 = new Scanner(animal);
        String dataAnimal = " ";
        for (int i = 0; i <= 6; i++) {
            dataMap = scanner1.nextLine();
            dataAnimal = scanner2.nextLine();
            for (int j = 0; j <= 8; j++) {
                map[i][j] = dataMap.charAt(j);
                animalData[i][j] = dataAnimal.charAt(j);
            }
            System.out.println();
        }//使用两个循环分别将两个地图录入Array map和animalData
        boolean player = true;
        //规定true为left，false为right
        printTheMap(animalData, map);
        //打印地图的方法
        System.out.println("输入help来了解游戏规则");
        char[][][] memory = new char[10000][7][9];
        //the array to undo and redo
        int currentTime = 0, toProtectRedo = 0;
        Scanner scanner3 = new Scanner(System.in);
        for (int p = 0; p <= 10000; p++) {
            String input;
            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 8; j++) {
                    memory[currentTime][i][j] = animalData[i][j];
                }
            }//copy the map to the array memory
            int line, row;
            char theAnimal;
            if (player) {
                System.out.println("left出牌:");
                input = scanner3.next();
                theAnimal = input.charAt(0);
                line = findPosition(theAnimal, player, animalData).charAt(0) - '0';
                row = findPosition(theAnimal, player, animalData).charAt(1) - '0';
            } else {
                System.out.println("right出牌:");
                input = scanner3.next();
                theAnimal = input.charAt(0);
                line = findPosition(theAnimal, player, animalData).charAt(0) - '0';
                row = findPosition(theAnimal, player, animalData).charAt(1) - '0';
            }//找到将要移动动物的坐标
            System.out.println(input);
            if (input.equals("help")) {
                System.out.println("指令介绍：\n\n" +
                        "1. 移动指令\n" +
                        "\t移动指令由两个部分组成。\n" +
                        "\t第一个部分是数字1-8，根据战斗力分别对应鼠(1),猫(2),狼(3),狗(4),豹(5),虎(6),狮(7),象(8)\n" +
                        "\t第二个部分是字母wasd中的一个,w对应上方向,a对应左方向,s对应下方向,d对应右方向\n" +
                        "\t比如指令\"1d\"表示鼠向右走,\"4w\"表示狗向左走\n\n" +
                        "2. 游戏指令\n" +
                        "\t输入 restart 重新开始游戏\n" +
                        "\t输入 help 查看帮助\n" +
                        "\t输入 undo 悔棋\n" +
                        "\t输入 redo 取消悔棋\n" +
                        "\t输入 exit 退出游戏");
                continue;
            }
            if (input.equals("exit")) break;
            if (input.equals("restart")) {
                for (int i = 0; i <= 6; i++) {
                    for (int j = 0; j <= 8; j++) {
                        animalData[i][j] = memory[0][i][j];
                    }
                }
                printTheMap(animalData, map);
                player = true;
                continue;
            }//重新开始就将地图还原为memory[0]所对应的数组即可
            if (input.equals("undo")) {
                if (currentTime - 1 < 0) {
                    System.out.println("已经退回至开头！");
                    continue;
                }
                for (int i = 0; i <= 6; i++) {
                    for (int j = 0; j <= 8; j++) {
                        animalData[i][j] = memory[currentTime - 1][i][j];
                    }
                }
                currentTime--;
                printTheMap(animalData, map);
                player = !player;
                toProtectRedo++;
                continue;
            }
            if (input.equals("redo")) {
                if (toProtectRedo <= 0) {
                    System.out.println("不能再反悔棋了！");
                    continue;
                }
                for (int i = 0; i <= 6; i++) {
                    for (int j = 0; j <= 8; j++) {
                        animalData[i][j] = memory[currentTime + 1][i][j];
                    }
                }
                currentTime++;
                printTheMap(animalData, map);
                player = !player;
                toProtectRedo--;
                continue;
            }
            if (findPosition(theAnimal, player, animalData).equals("can't find") | input.length() > 2) {
                System.out.println("不能识别指令" + "“" + input + "”");
                continue;
            }//若输入指令不正确则打出提示
            switch (input.charAt(1)) {
                //根据方向指令的不同分为四种情况，分别判断越界，水，大小问题这三个违规情况。
                case 'w':
                    if (line - 1 < 0) {
                        System.out.println("你越界了。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 1) {
                        System.out.println("该动物不能入水。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 3) {
                        System.out.println("对面鼠在河里。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 2) {
                        if ((judgeTheMove(animalData[line][row], animalData[line - 3][row], map[line - 3][row], map[line][row], player)) == 3) {
                            System.out.println("不能吃自己的棋子。");
                            continue;
                        }
                        if ((judgeTheMove(animalData[line][row], animalData[line - 3][row], map[line - 3][row], map[line][row], player)) == 4) {
                            System.out.println("对方比你强大。");
                            continue;
                        } else {
                            animalData[line - 3][row] = animalData[line][row];
                            animalData[line][row] = '0';
                            currentTime++;
                        }
                    } else {
                        switch ((judgeTheMove(animalData[line][row], animalData[line - 1][row], map[line - 1][row], map[line][row], player))) {
                            case 2:
                                System.out.println("不能进入自己的窝。");
                                continue;
                            case 3:
                                System.out.println("不能吃自己的棋子。");
                                continue;
                            case 4:
                                System.out.println("对方比你强大。");
                                continue;
                            case 5:
                                System.out.println("水中鼠不能吃象。");
                                continue;
                            default:
                                animalData[line - 1][row] = animalData[line][row];
                                animalData[line][row] = '0';
                                currentTime++;
                        }
                    }
                    break;
                case 's':
                    if (line + 1 > 6) {
                        System.out.println("你越界了。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 1) {
                        System.out.println("该动物不能入水。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 3) {
                        System.out.println("对面鼠在河里。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 2) {
                        if ((judgeTheMove(animalData[line][row], animalData[line + 3][row], map[line + 3][row], map[line][row], player)) == 3) {
                            System.out.println("不能吃自己的棋子。");
                            continue;
                        }
                        if ((judgeTheMove(animalData[line][row], animalData[line + 3][row], map[line + 3][row], map[line][row], player)) == 4) {
                            System.out.println("对方比你强大。");
                            continue;
                        } else {
                            animalData[line + 3][row] = animalData[line][row];
                            animalData[line][row] = '0';
                            currentTime++;
                        }
                    } else {
                        switch (judgeTheMove(animalData[line][row], animalData[line + 1][row], map[line + 1][row], map[line][row], player)) {
                            case 2:
                                System.out.println("不能进入自己的窝。");
                                continue;
                            case 3:
                                System.out.println("不能吃自己的棋子。");
                                continue;
                            case 4:
                                System.out.println("对方比你强大。");
                                continue;
                            case 5:
                                System.out.println("水中鼠不能吃象。");
                                continue;
                            default:
                                animalData[line + 1][row] = animalData[line][row];
                                animalData[line][row] = '0';
                                currentTime++;
                        }
                    }
                    break;
                case 'a':
                    if (row - 1 < 0) {
                        System.out.println("你越界了。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 1) {
                        System.out.println("该动物不能入水。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 3) {
                        System.out.println("对面鼠在河里。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 2) {
                        if ((judgeTheMove(animalData[line][row], animalData[line][row - 4], map[line][row - 4], map[line][row], player)) == 3) {
                            System.out.println("不能吃自己的棋子。");
                            continue;
                        }
                        if ((judgeTheMove(animalData[line][row], animalData[line][row - 4], map[line][row - 4], map[line][row], player)) == 4) {
                            System.out.println("对方比你强大。");
                            continue;
                        } else {
                            animalData[line][row - 4] = animalData[line][row];
                            animalData[line][row] = '0';
                            currentTime++;
                        }
                    } else {
                        switch (judgeTheMove(animalData[line][row], animalData[line][row - 1], map[line][row - 1], map[line][row], player)) {
                            case 2:
                                System.out.println("不能进入自己的窝。");
                                continue;
                            case 3:
                                System.out.println("不能吃自己的棋子。");
                                continue;
                            case 4:
                                System.out.println("对方比你强大。");
                                continue;
                            case 5:
                                System.out.println("水中鼠不能吃象。");
                                continue;
                            default:
                                animalData[line][row - 1] = animalData[line][row];
                                animalData[line][row] = '0';
                                currentTime++;
                        }
                    }
                    break;
                case 'd':
                    if (row + 1 > 8) {
                        System.out.println("你越界了。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 1) {
                        System.out.println("该动物不能入水。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 3) {
                        System.out.println("对面鼠在河里。");
                        continue;
                    }
                    if (judgeWater(line, row, input.charAt(1), animalData, map, player) == 2) {
                        if ((judgeTheMove(animalData[line][row], animalData[line][row + 4], map[line][row + 4], map[line][row], player)) == 3) {
                            System.out.println("不能吃自己的棋子。");
                            continue;
                        }
                        if ((judgeTheMove(animalData[line][row], animalData[line][row + 4], map[line][row + 4], map[line][row], player)) == 4) {
                            System.out.println("对方比你强大。");
                            continue;
                        } else {
                            animalData[line][row + 4] = animalData[line][row];
                            animalData[line][row] = '0';
                            currentTime++;
                        }
                    } else {
                        switch (judgeTheMove(animalData[line][row], animalData[line][row + 1], map[line][row + 1], map[line][row], player)) {
                            case 2:
                                System.out.println("不能进入自己的窝。");
                                continue;
                            case 3:
                                System.out.println("不能吃自己的棋子。");
                                continue;
                            case 4:
                                System.out.println("对方比你强大。");
                                continue;
                            case 5:
                                System.out.println("水中鼠不能吃象。");
                                continue;
                            default:
                                animalData[line][row + 1] = animalData[line][row];
                                animalData[line][row] = '0';
                                currentTime++;
                        }
                    }
                    break;
                default:
                    System.out.println(input.charAt(1) + "不对应任何方向");
                    continue;
            }
            printTheMap(animalData, map);
            toProtectRedo = 0;
            //修改后的代码可以打出是哪一方赢了。
            if (whetherWin(animalData, map) == 1) {
                String winner = player ? ("左方") : ("右方");
                System.out.println("恭喜，" + winner + "已获胜。");
                break;
            }
            player = !player;
        }
    }

    /*此方法是用来在输入指令后来找到所要移动的动物所对应的坐标的，但因为左方的动物在数组中是字母，所以要用theAnimal = (char) (theAnimal - '1' + 'a');，
    右方可以不直接转化，再通过animal数组都进行扫描，找到相应动物，将横纵坐标一起储存在字符串str中返回（如6和1返回61），找不到动物，返回can’t find*/
    public static String findPosition(char theAnimal, boolean player, char[][] animal) {
        String str = "";
        if (player)
            theAnimal = (char) (theAnimal - '1' + 'a');
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 9; j++) {
                if (animal[i][j] == theAnimal) {
                    str = str + i + j;
                    return str;
                }
            }
        }
        return "can't find";
    }

    /*此方法专门用于打印地图，因此无返回值类型，输入动物与地形地图，开始两层循环，若这个格子上有动物，则打印动物，没有动物则打印地形。（具体通过switch实现）*/
    public static void printTheMap(char[][] animalData, char[][] map) {
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 8; j++) {
                if (animalData[i][j] == '0') {
                    switch (map[i][j]) {
                        case '0':
                            System.out.print("    ");
                            break;
                        case '1':
                            System.out.print(" 水 ");
                            break;
                        case '2':
                            System.out.print(" 陷 ");
                            break;
                        case '3':
                            System.out.print(" 窝 ");
                            break;
                        case '4':
                            System.out.print(" 陷 ");
                            break;
                        case '5':
                            System.out.print(" 窝 ");
                            break;
                        default:
                            System.out.print(' ');
                    }
                } else {
                    switch (animalData[i][j]) {
                        case '0':
                            System.out.print("    ");
                        case '1':
                            System.out.print("鼠1 ");
                            break;
                        case '2':
                            System.out.print("猫2 ");
                            break;
                        case '3':
                            System.out.print("狼3 ");
                            break;
                        case '4':
                            System.out.print("狗4 ");
                            break;
                        case '5':
                            System.out.print("豹5 ");
                            break;
                        case '6':
                            System.out.print("虎6 ");
                            break;
                        case '7':
                            System.out.print("狮7 ");
                            break;
                        case '8':
                            System.out.print("象8 ");
                            break;
                        case 'a':
                            System.out.print("1鼠 ");
                            break;
                        case 'b':
                            System.out.print("2猫 ");
                            break;
                        case 'c':
                            System.out.print("3狼 ");
                            break;
                        case 'd':
                            System.out.print("4狗 ");
                            break;
                        case 'e':
                            System.out.print("5豹 ");
                            break;
                        case 'f':
                            System.out.print("6虎 ");
                            break;
                        case 'g':
                            System.out.print("7狮 ");
                            break;
                        case 'h':
                            System.out.print("8象 ");
                            break;

                        default:
                            System.out.println();
                    }
                }
            }
            System.out.println();
        }
    }

    /*此方法主要用于判断动物间的互吃关系
    拿对于左方举例，先判断该动物的行为是否是水中鼠要吃大象，如果是，返回5
    之后判断要进的格子是否为自己的窝，如果是，则返回2，再判断要吃的棋子是否为自己的棋子，如果是，返回3，之后，若下一格有动物，判断他是否在陷阱里，是的话直接吃掉返回1，
    不在陷阱中，判断此动物是否可吃（象与鼠单独写出），可吃返回1，不可吃返回4
    PS 违规操作的提示都在主方法中。*/
    public static int judgeTheMove(char yourOwnAnimal, char theAnimalNextCell, char theTerrainNextCell, char yourOwnTerrain, boolean player) {
        if (player) {
            if (yourOwnAnimal == 'a' & yourOwnTerrain == '1' & theAnimalNextCell == '8') return 5;
            if (theTerrainNextCell == '3')
                return 2;
            if (theAnimalNextCell - 'a' >= 0 && theAnimalNextCell - 'a' < 8) return 3;
            if (theAnimalNextCell != '0') {
                if (theTerrainNextCell == '2') {
                    return 1;
                } else if ((yourOwnAnimal - 'a' >= theAnimalNextCell - '1' | (yourOwnAnimal == 'a' & theAnimalNextCell == '8')) & !(yourOwnAnimal == 'h' & theAnimalNextCell == '1')) {
                    return 1;
                } else {
                    return 4;
                }
            }
        }
        if (!player) {
            if (yourOwnAnimal == '1' & yourOwnTerrain == '1' & theAnimalNextCell == 'h') return 5;
            if (theTerrainNextCell == '5')
                return 2;
            if (theAnimalNextCell - '1' >= 0 && theAnimalNextCell - '1' < 8) return 3;
            if (theAnimalNextCell != '0') {
                if (theTerrainNextCell == '4') {
                    return 1;
                } else if ((yourOwnAnimal - '1' >= theAnimalNextCell - 'a' | (yourOwnAnimal == '1' & theAnimalNextCell == 'h')) & !(yourOwnAnimal == '8' & theAnimalNextCell == 'a')) {
                    return 1;
                } else {
                    return 4;
                }
            }
        }
        return 1;
    }

    /* 此方法用于判断有关小河方面的规则，这里我依然采用分方向解决的办法，开始是有一个判断句，说明一旦要移动的是鼠，这个方法永远只输出0，
    如果要移动的棋子不是鼠，则分为四个方向，拿w方向举例第一个判断是要判断进入的格子是否为水，不是返回0，是的话接着判断是否要移动的是狮虎，
    不是的话返回1，禁止跳河，不是则判断之间是否有敌方鼠挡路（所以开始的时候要以player作为参数之一），有鼠挡路返回3，无鼠返回2，在主界面中
    执行跳河操作。 */
    public static int judgeWater(int line, int row, char direction, char[][] animalData, char[][] map, boolean player) {
        if (animalData[line][row] != '1' && animalData[line][row] != 'a') {
            switch (direction) {
                case 'w':
                    if (map[line - 1][row] == '1') {
                        if (animalData[line][row] != '6' & animalData[line][row] != '7' &
                                animalData[line][row] != 'f' & animalData[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((player == false & (animalData[line - 1][row] != 'a' & animalData[line - 2][row] != 'a')) |
                                    (player == true & (animalData[line - 1][row] != '1' & animalData[line - 2][row] != '1'))) {
                                return 2;
                            } else {
                                return 3;
                            }

                        }
                    } else return 0;
                case 's':
                    if (map[line + 1][row] == '1') {
                        if (animalData[line][row] != '6' & animalData[line][row] != '7' &
                                animalData[line][row] != 'f' & animalData[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((player == false & animalData[line + 1][row] != 'a' & animalData[line + 2][row] != 'a') |
                                    (player == true & animalData[line + 1][row] != '1' & animalData[line + 2][row] != '1')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                case 'a':
                    if (map[line][row - 1] == '1') {
                        if (animalData[line][row] != '6' & animalData[line][row] != '7' &
                                animalData[line][row] != 'f' & animalData[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((player == false & animalData[line][row - 1] != 'a' & animalData[line][row - 2] != 'a' & animalData[line][row - 3] != 'a') |
                                    (player == true & animalData[line][row - 1] != '1' & animalData[line][row - 2] != '1' & animalData[line][row - 3] != '1')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                case 'd':
                    if (map[line][row + 1] == '1') {
                        if (animalData[line][row] != '6' & animalData[line][row] != '7' &
                                animalData[line][row] != 'f' & animalData[line][row] != 'g') {
                            return 1;
                        } else {
                            if ((player == false & animalData[line][row + 1] != 'a' & animalData[line][row + 2] != 'a' & animalData[line][row + 3] != 'a') |
                                    (player == true & animalData[line][row + 1] != '1' & animalData[line][row + 2] != '1' & animalData[line][row + 3] != '1')) {
                                return 2;
                            } else {
                                return 3;
                            }
                        }
                    } else return 0;
                default:
                    return 4;
            }
        } else return 0;
    }

    /*此方法是主要的判断胜负的方法，因为之前在judgeTheMove函数中已经确定了动物不能进入自己的窝，所以这里只需判断在相应窝的位置是否有动物即可。
    之后的两个胜利条件被我分装在3个其他方法里。*/
    public static int whetherWin(char[][] animal, char[][] map) {
        if (animal[3][0] != '0' | animal[3][8] != '0') {
            return 1;
        } else {
            if (whetherNoChess(animal) == 1) {
                return 1;
            } else {
                if (whetherCanIMove(animal, map) == 1) {
                    return 1;
                } else return 0;
            }
        }
    }

    /*这个方法用于判断是否有一方的动物被吃光了,用三层循环扫描动物，第一层是从鼠到象的循环，第二三层是对于地图的循环，如果发现了要找的动物，
    则使得相应变量+1，若有一方等于零，则游戏结束。（重要变量int theAliveAnimalOfRightPlayer , theAliveAnimalOfLeftPlayer 分别指右方和左方活着的动物的数量）*/
    public static int whetherNoChess(char[][] animal) {
        int theAliveAnimalOfRightPlayer = 0, theAliveAnimalOfLeftPlayer = 0;
        for (int q = 1; q <= 8; q++) {
            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 8; j++) {
                    if (animal[i][j] == (char) (q + '0')) theAliveAnimalOfRightPlayer++;
                    if (animal[i][j] == (char) (q + 'a' - 1)) theAliveAnimalOfLeftPlayer++;
                }
            }
        }
        if (theAliveAnimalOfRightPlayer == 0 | theAliveAnimalOfLeftPlayer == 0) {
            return 1;
        } else return 0;
    }

    /*   这两个方法都是用来判断无子可动的方法（第二个是第一个的一部分）
       定义int类型的重要变量theAnimalAliveOfRight , theAnimalAliveOfLeft , theNumberOfLeftAnimalsToMove , theNumberOfRightAnimalsToMove
       来表示不能动的动物数和活着的动物数，然后用和与whetherNoChess类似的方法算出存活的动物数，之后，在toJudgeWhetherCanIMove方法中向上下左右移动每一个棋子
       （其中调用了第三个和第四个方法），若上下左右都不可移动，则输出1，否则输出0，这样在whetherCanIMove的循环中就能够算出可移动动物的数量，有一方可移动的动
       物数与存活的数目相等，则返回1，有一方获胜。*/
    public static int whetherCanIMove(char[][] animal, char[][] map) {
        int theAnimalAliveOfRight = 0, theAnimalAliveOfLeft = 0, theNumberOfLeftAnimalsToMove = 0, theNumberOfRightAnimalsToMove = 0;
        for (int q = 1; q <= 8; q++) {
            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 8; j++) {
                    if (animal[i][j] == (char) (q + '0')) {//右方棋子
                        theAnimalAliveOfRight++;
                        theNumberOfRightAnimalsToMove += toJudgeWhetherCanIMove(i, j, animal, map, false);
                    }
                    if (animal[i][j] == (char) (q + 'a' - 1)) {//左方棋子
                        theAnimalAliveOfLeft++;
                        theNumberOfLeftAnimalsToMove += toJudgeWhetherCanIMove(i, j, animal, map, true);
                    }
                }
            }
        }
        if (theNumberOfLeftAnimalsToMove == theAnimalAliveOfLeft | theNumberOfRightAnimalsToMove == theAnimalAliveOfRight)
            return 1;
        else return 0;
    }

    public static int toJudgeWhetherCanIMove(int line, int row, char[][] animal, char[][] map, boolean player) {
        int p = 0;
        if (line > 0) {
            if (judgeWater(line, row, 'w', animal, map, true) == 0) {
                if (judgeTheMove(animal[line][row], animal[line - 1][row], map[line - 1][row], map[line][row], player) == 1) {
                    p++;
                }
            }
        }
        if (line < 6) {
            if (judgeWater(line, row, 's', animal, map, true) == 0) {
                if (judgeTheMove(animal[line][row], animal[line + 1][row], map[line + 1][row], map[line][row], player) == 1) {
                    p++;
                }
            }
        }
        if (row > 0) {
            if (judgeWater(line, row, 'a', animal, map, true) == 0) {
                if (judgeTheMove(animal[line][row], animal[line][row - 1], map[line][row - 1], map[line][row], player) == 1) {
                    p++;
                }
            }
        }
        if (row < 8) {
            if (judgeWater(line, row, 'd', animal, map, true) == 0) {
                if (judgeTheMove(animal[line][row], animal[line][row + 1], map[line][row + 1], map[line][row], player) == 1) {
                    p++;
                }
            }
        }
        if (p == 0) return 1;
        else return 0;
    }
}


