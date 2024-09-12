import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class Pair {
	int x;
	int y;
	int d; // 방향 상좌하우 -> 0,1,2,3

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
		this.d = -1;
	}

	public Pair(int x, int y, int d) {
		this.x = x;
		this.y = y;
		this.d = d;
	}

	int getDistance(int x, int y){
		return Math.abs(x - this.x) + Math.abs(y - this.y);
	}
}
public class Main {


	static int N, M, H, K; 
	static int answer;
	static Pair[] thieves;
	static Pair[] trees;
	static Pair tagger;
	static int[][] dir;
	static int[][] dxy = {{-1,0},{0,1},{1,0},{0,-1}};
	static int[][] rdxy = {{1,0},{0,1},{-1,0},{0,-1}};
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken()); // 격자 크기 (반드시 홀수)
		M = Integer.parseInt(st.nextToken()); // 도망자 수
		H = Integer.parseInt(st.nextToken()); // 나무 수
		K = Integer.parseInt(st.nextToken()); // 총 턴 횟수

		thieves = new Pair[M];
		trees = new Pair[H];

		answer = 0;

		// 술래는 가운데서 시작하고 
		// 도망자는 좌우로 이동하면 우, 상하로 이동하면 하 시작
		// 나무와 도망자는 초기에 겹쳐있을 수 있다.

		// 도망자 정보 
		for(int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());

			int tempX = Integer.parseInt(st.nextToken())-1;
			int tempY = Integer.parseInt(st.nextToken())-1;
			int tempD = Integer.parseInt(st.nextToken());

			if(tempD == 1) {
				tempD = 1; // 우
			}else if(tempD == 2) {
				tempD = 2; // 하
			}

			thieves[i] = new Pair(tempX, tempY, tempD);
		}

		// 나무 정보
		for(int i = 0; i < H; i++) {
			st = new StringTokenizer(br.readLine());
			trees[i] = new Pair(Integer.parseInt(st.nextToken())-1, Integer.parseInt(st.nextToken())-1);
		}

		// 이제 술래 초기값 정해주고 바로 방향정해서 시뮬 돌리면 됨.
		tagger = new Pair(N/2, N/2, 0);

		simulation();

		System.out.println(answer);
	}

	static void simulation() {
		//술래는 소용돌이 방향으로 나가야함.
		// K번 만큼 진행
		int move = 1;
		int moveCnt = 0; // 2 가 될때마다 move를 1씩 올려줌
		int cnt = 0;
		dir = dxy;
		boolean rFlag = false;

		for(int t = 1; t <= K; t++) {
			moveThieves();

			int nx = tagger.x + dir[tagger.d][0];
			int ny = tagger.y + dir[tagger.d][1];

			tagger.x = nx;
			tagger.y = ny;
			
			cnt++;
			if(!rFlag && nx == 0 && ny == 0) {
				rFlag = true;
				dir = rdxy;
				tagger.d = 0;
				moveCnt = -1;
				cnt = 0;
				move = N-1;
			}

			else if(rFlag && nx == N/2 && ny == N/2) {
				rFlag = false;
				dir = dxy;
				tagger.d = 0;
				cnt = 0;
				moveCnt = 0;
				move = 1;
			}
			else {
				if(cnt == move) {
					cnt = 0;
					moveCnt++;
					tagger.d = (tagger.d+1) % 4;

					if(moveCnt == 2) {
						moveCnt = 0;
						if(rFlag) move -= 1;
						else move += 1;
					}
				}
			}





			countScore(t);

		}
	}	

	static void moveThieves() {
		for(int i = 0; i < M; i++) {
			if(thieves[i].d == -1) continue; // 잡힌 경우
			if(thieves[i].getDistance(tagger.x, tagger.y) > 3) continue;

			int curX = thieves[i].x;
			int curY = thieves[i].y;
			int curD = thieves[i].d;

			int nx = curX + dxy[curD][0];
			int ny = curY + dxy[curD][1];

			// 어쨋든 다음위치에 술래가 있어도 벗어나면 방향전환은 해줘야함.
			if(nx < 0 || ny < 0 || nx >= N || ny >= N) {
				thieves[i].d = (curD + 2) % 4;
				curD = thieves[i].d;

				nx = curX + dxy[curD][0];
				ny = curY + dxy[curD][1];
			}

			// 다음위치에 술래가 있다면
			if(tagger.getDistance(nx, ny) == 0) continue;

			// 술래가 없다면
			thieves[i].x = nx;
			thieves[i].y = ny;			
		}
	}

	static void countScore(int turn) {
		int curD = tagger.d;

		for(int i = 0; i < 3; i++) { // 술래의 시야는 언제나 3
			int nx = tagger.x + dir[curD][0] * i;
			int ny = tagger.y + dir[curD][1] * i;

			if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;

			for(int j = 0; j < M; j++) { // 도망자 위치 체크
				if(thieves[j].d == -1) continue;

				if(thieves[j].getDistance(nx, ny) == 0 && !checkTreeHide(nx, ny)) {
					thieves[j].d = -1;
					answer += turn;
				}
			}
		}
	}

	static boolean checkTreeHide(int x, int y) {
		for(int i = 0; i < H; i++) {
			if(trees[i].x == x && trees[i].y == y) return true;
		}

		return false;
	}
}