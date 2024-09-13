import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;


class Group{
	int cnt;
	int number;

	public Group(int cnt, int number) {
		this.cnt = cnt;
		this.number = number;
	}
}

public class Main{
	static int N, numOfGroup, answer;
	static int[][] board;
	static Group[][] marking;
	static boolean[][] visited;
	static HashMap<Group, Integer> groupAdjCnt;
	static int[][] dxy = {{-1,0},{1,0},{0,-1},{0,1}};
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		N = Integer.parseInt(br.readLine());
		answer = 0;

		board = new int[N][N];

		// 입력 데이터 전처리
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		}


		// 3 번 점수를 측정
		for(int t=0; t < 4; t++) {
			visited = new boolean[N][N];	
			marking = new Group[N][N];


			// 그룹화 및 그룹화 객체를 marking 배열에 저장
			for(int i=0; i < N; i++) {
				for(int j=0; j < N; j++) {
					if(marking[i][j] != null) continue;

					groupMethod(i,j, board[i][j]);
					markGroup(i,j);
				}
			}			

			visited = new boolean[N][N];
			// 그룹화 된 그룹들을 바탕으로 계산 수행
			for(int i=0; i < N; i++) {
				for(int j=0; j < N; j++) {
					if(visited[i][j]) continue;

					groupAdjCnt = new HashMap<>(); // 그룹 하나에 대해서 계산을 수행할 때 초기화
					groupDfs(i, j, marking[i][j]);
					calculateScore(i,j);
				}
			}

			// 끝나면 한번씩 회전을 시켜줘야함.
			board = rotate();
		}

		System.out.println(answer);
	}

	static void groupDfs(int i, int j, Group base) {
		if(i < 0 || j < 0 || i >= N || j >= N || visited[i][j]) return;

		if(marking[i][j] == base) {
			visited[i][j] = true; // 현재 탐색중인 그룹에 대해서만 -> 해당 그룹에 대한 계산은 이번에 끝냄. 4방향 탐색후
			
			for(int d = 0; d < 4; d++) {
				groupDfs(i+dxy[d][0], j+dxy[d][1], base);
			}
		}
		else {
			groupAdjCnt.put(marking[i][j], groupAdjCnt.getOrDefault(marking[i][j], 0) + 1);
			return;
		}
	}

	static void calculateScore(int x, int y) {
		Group base = marking[x][y];
		Set<Group> adjList = groupAdjCnt.keySet();

		for(Group adjGroup : adjList) {
			int score = (base.cnt + adjGroup.cnt) * base.number * adjGroup.number * groupAdjCnt.get(adjGroup);
			if(score != 0) {
				answer += score;
			}
		}
	}

	static int[][] rotate() {
		int[][] rotated = new int[N][N];

		// 1. 십자모양 반시계 회전
		// 십자 가로축 복사 (기존 입력 board 기준 회전)
		for(int i=0; i<N; i++) {
			rotated[(N-1) - i][N/2] = board[N/2][i];
		}
		// 십자 세로축 복사 (기존 입력 board 기준 회전)
		for(int i=0; i<N; i++) {
			rotated[N/2][i] = board[i][N/2];
		}

		// 2. 각 네 구역 시계방향 회전
		rotateQuarter(0, 0, rotated); 
		rotateQuarter(0, N/2+1, rotated); 
		rotateQuarter(N/2+1, 0, rotated); 
		rotateQuarter(N/2+1, N/2+1, rotated); 

		return rotated;
	}

	static void rotateQuarter(int x, int y, int[][] rotated){
		// 반복문의 iterator를 증감 값으로 사용 기본 x,y좌표를 기준
		// 앞서바뀌는 값은 j를 통해 조작, 이후 느리게 바뀌는값은 i를 통해 조작
		for(int i = 0; i < N/2; i++) {
			for(int j = 0; j < N/2; j++) {
				rotated[x+i][y+j] = board[x+(N/2-1)-j][y+i];
			}
		}
	}

	static void groupMethod(int i, int j, int num) {
		if(i < 0 || j < 0 || i >= N || j >= N) return;
		if(visited[i][j] || board[i][j] != num) return;		

		visited[i][j] = true;

		for(int d = 0; d < 4; d++) {
			groupMethod(i+dxy[d][0], j+dxy[d][1], num);
		}
	}


	static void markGroup(int x, int y) {
		int cnt = 0;
		int num = board[x][y];


		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				if(visited[i][j]) cnt++;
			}
		}

		Group group = new Group(cnt, num);

		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				if(visited[i][j]) {
					marking[i][j] = group;
					visited[i][j] = false;
				}
			}
		}
	}


}