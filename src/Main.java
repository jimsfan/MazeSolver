package com.company;

import javax.swing.text.StyledEditorKit;
import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.util.Queue;
import java.util.*;

class Cell{
    int x;
    int y;
    int gCost;
    int hCost;
    Cell parent;

    public Cell(int x, int y, Cell parent){
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public Cell(int x, int y, Cell parent, int gCost, int hCost){
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
    }

    public Cell getParent(){
        return this.parent;
    }
}

class MazeSolver {

    //4 possible moves
    static int[] move_x = {1, -1, 0, 0};
    static int[] move_y = {0, 0, 1, -1};

    // Using DFS algorithm, does not guarantee shortest path
    static Boolean MazeBackTracker(char[][] maze, Cell cell, int m, int n){
        // Boundary conditions
        if (!isValidMove(maze, cell, m, n)) return false;

        // Found exit
        if (maze[cell.x][cell.y] == 'E') return true;

        // Mark current node as visited
        maze[cell.x][cell.y] = 'X';

        //Recursively visit the next nodes
        for (int move = 0; move < 4; move++){
            Cell nextCell = new Cell(cell.x + move_x[move], cell.y + move_y[move], cell);
            if (MazeBackTracker(maze, nextCell, m, n)) return true;
        }

        // Backtrack
        maze[cell.x][cell.y] = ' ';
        return false;
    }

    // Using BFS algorithm
    static Cell BFS(char[][] maze, Cell cell, int m, int n){
        Queue<Cell> q = new LinkedList<Cell>();
        q.add(cell);
        while(!q.isEmpty()){
            Cell cur = q.remove();

            // Found exit
            if (maze[cur.x][cur.y] == 'E') return cur.getParent();

            // Mark as visited
            maze[cur.x][cur.y] = 'X';
            for (int move = 0; move < 4; move++){
                Cell nextCell = new Cell(cur.x + move_x[move], cur.y + move_y[move], cur);
                if (isValidMove(maze, nextCell, m, n)) {
                    q.add(nextCell);
                }
            }
        }
        return null;
    }

    // Using AStar algorithm
    static Cell AStarSolver(char[][] maze, Cell cell, Cell goal, int m, int n){
        List<Cell> settledCells = new ArrayList<Cell>();
        List<Cell> unsettledCells = new ArrayList<Cell>();
        cell.gCost = 0;
        cell.hCost = cell.gCost + Heuristic(cell, goal);
        unsettledCells.add(cell);

        while(!unsettledCells.isEmpty()){
            int cellwlowestcost = GetCellwithLowestCost(unsettledCells);
            Cell cur = unsettledCells.get(cellwlowestcost);

            // Found exit
            if (cur.x == goal.x && cur.y == goal.y) return cur.getParent();
            unsettledCells.remove(cellwlowestcost);
            settledCells.add(cur);

            for (int move = 0; move < 4; move++){
                Cell nextCell = new Cell(cur.x + move_x[move], cur.y + move_y[move], cur, 0, 0);
                nextCell.hCost = Heuristic(nextCell, goal);
                nextCell.gCost = cur.gCost + 1 + nextCell.hCost;
                if (isValidMoveAStar(maze, nextCell, m, n) && !isCellinList(settledCells, nextCell)) {
                    if (!isCellinList(unsettledCells, nextCell)) {
                        unsettledCells.add(nextCell);
                    } else {
                        Cell vistedCell = GetCellfromList(unsettledCells, nextCell);
                        if (nextCell.gCost < vistedCell.gCost){
                            vistedCell.gCost = nextCell.gCost;
                            vistedCell.parent = nextCell.parent;
                        }
                    }

                }
            }
        }

        return null;
    }

    // Implementation of Manhattan distance
    static int Heuristic (Cell cell, Cell goal){
        return Math.abs(goal.x - cell.x) + Math.abs(goal.y - cell.y);
    }

    static int GetCellwithLowestCost(List<Cell> path){
        int cost = Integer.MAX_VALUE;
        int posn = 0;
        for (int i = 0; i < path.size(); i++){
            if (path.get(i).gCost < cost) {
                cost = path.get(i).gCost;
                posn = i;
            }
        }
        return posn;
    }

    static Cell GetCellfromList(List<Cell> path, Cell cell){
        for (int i = 0; i < path.size(); i++){
            if (CompareCells(path.get(i), cell)) return path.get(i);
        }
        return null;
    }

    static Boolean isCellinList(List<Cell> path, Cell cell){
        for (int i = 0; i < path.size(); i++){
            if (CompareCells(path.get(i), cell)) return true;
        }
        return false;
    }

    static Boolean CompareCells(Cell a, Cell b){
        if (a.x == b.x && a.y == b.y) return true;
        else return false;
    }

    static Boolean isValidMove(char[][] maze, Cell cell, int m, int n){
        // Boundary conditions
        if (cell.x < 0 || cell.x >= m || cell.y < 0 || cell.y >= n || maze[cell.x][cell.y] == '#'
                || maze[cell.x][cell.y] == 'X') return false;
        return true;
    }

    static Boolean isValidMoveAStar(char[][] maze, Cell cell, int m, int n){
        // Boundary conditions
        if (cell.x < 0 || cell.x >= m || cell.y < 0 || cell.y >= n || maze[cell.x][cell.y] == '#') return false;
        return true;
    }
}

public class Main {
    public static void main(String[] args) {
        // write your code here

        try {
            // Specify input path here
            Scanner sc = new Scanner(new File("C:\\Users\\jimfa\\Desktop\\mazer\\mazer\\test.txt"));

            // Comment this out if you do not want to output to file
            PrintStream output = new PrintStream(new File("Solved_Maze.txt"));
            System.setOut(output);

            // Control which kind of algorithm you want to use, please turn only ONE on
            Boolean useDFS = false;
            Boolean useBFS = true;
            Boolean useAStar = false;

            int m = sc.nextInt();
            int n = sc.nextInt();
            int startx = sc.nextInt();
            int starty = sc.nextInt();
            Cell cell = new Cell(startx, starty, null, 0, 0);
            int endx = sc.nextInt();
            int endy = sc.nextInt();
            Cell goal = new Cell (endx, endy, null, 0,0);
            char[][] maze = new char[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    int temp = sc.nextInt();
                    if (temp == 1) {
                        maze[i][j] = '#';
                    } else if (temp == 0) {
                        maze[i][j] = ' ';
                    }
                    if (i == startx && j == starty) {
                        maze[i][j] = 'S';
                    } else if (i == endx && j == endy) {
                        maze[i][j] = 'E';
                    }
                }
            }
            sc.close();

            // Solve maze using backtracking or DFS (this does not guarantee shortest path)
            if (useDFS == true){
                if (MazeSolver.MazeBackTracker(maze, cell, m, n)) {
                    // Printing the maze
                    maze[startx][starty] = 'S';
                    PrintMaze(maze, m, n);
                } else {
                    System.out.println("No path found!");
                }
            }

            // Solve maze using BFS
            if (useBFS == true) {
                Cell mazeExit = MazeSolver.BFS(maze, cell, m, n);
                ClearMaze(maze, m, n);
                PrintMazefromExit(maze,cell,mazeExit,m,n);
            }

            // Solve maze using AStar
            if (useAStar == true){
                Cell mazeExit = MazeSolver.AStarSolver(maze, cell, goal, m, n);
                PrintMazefromExit(maze,cell,mazeExit,m,n);
            }
        } catch (Exception e) {
            System.out.println("File not found");
        }
    }

    static void PrintMaze(char[][] maze, int m, int n){
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    static void PrintMazefromExit(char[][] maze, Cell start, Cell exit, int m, int n){
        if (exit != null) {
            // Mark the shortest path with X
            while (exit.getParent() != null) {
                maze[exit.x][exit.y] = 'X';
                exit = exit.getParent();
            }
            // Printing the maze
            maze[start.x][start.y] = 'S';
            PrintMaze(maze, m, n);
        } else {
            System.out.println("No path found!");
        }
    }

    static void ClearMaze(char[][] maze, int m, int n){
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (maze[i][j] == 'X') maze[i][j] = ' ';
            }
        }
    }
}
