import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class Pair {
	int x;
	int y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

public class Main {
	static int N, M, K;
	static Pair cubePos;
	static int[] cube;
	static int[][] dxy = {{0,1},{0,-1},{-1,0},{1,0}}; // 동 / 서 / 북 / 남 순
	static int[][] board;
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		cubePos = new Pair(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
		K = Integer.parseInt(st.nextToken());
		board = new int[N][M];

		cube = new int[6];

		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < M; j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		st = new StringTokenizer(br.readLine());
		for(int k = 0; k < K; k++) {
			int dir = Integer.parseInt(st.nextToken()) - 1;
			
			int nx = cubePos.x + dxy[dir][0];
			int ny = cubePos.y + dxy[dir][1];
			
			if(nx < 0 || ny < 0 || nx >= N || ny >= M) continue;
			
			rotateCube(dir);
			
			if(board[nx][ny] == 0) {
				board[nx][ny] = cube[5];
			}
			else if(board[nx][ny] != 0) {
				cube[5] = board[nx][ny];
				board[nx][ny] = 0;
			}
			
			cubePos.x = nx;
			cubePos.y = ny;
			
			System.out.println(cube[0]);
		}
	}


	static void rotateCube(int dir) {
		if(dir == 0) {
			cube = new int[]{cube[4], cube[1], cube[0], cube[3], cube[5], cube[2]};
		}
		else if(dir == 1) {
			cube = new int[]{cube[2], cube[1], cube[5], cube[3], cube[0], cube[4]};
		}
		else if(dir == 2) {
			cube = new int[]{cube[1], cube[5], cube[2], cube[0], cube[4], cube[3]};
		}
		else if(dir == 3) {
			cube = new int[]{cube[3], cube[0], cube[2], cube[5], cube[4], cube[1]};
		}
	}
}