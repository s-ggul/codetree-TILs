import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

class Pair implements Comparable<Pair>{
	int x;
	int y;
	int num;

	public Pair(int x, int y, int num) {
		this.x = x;
		this.y = y;
		this.num = num;
	}

	@Override
	public int compareTo(Pair o) {
		return this.num - o.num;
	}
}

public class Main{
	static int N, M, K, answer;
	static int[][] board, marking;
	static boolean[][] visited;
	static int[][] dxy = {{-1,0},{1,0},{0,-1},{0,1}};
	static HashMap<Integer, ArrayList<Pair>> teams;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken()); // 격자 크기
		M = Integer.parseInt(st.nextToken()); // 팀 수
		K = Integer.parseInt(st.nextToken()); // 라운드 수
		board = new int[N][N];

		teams = new HashMap<>();

		// 입력 격자 전처리
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		// 각 팀 별 마킹
		marking = new int[N][N];
		visited = new boolean[N][N];
		int teamNum = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(board[i][j]!=0 && !visited[i][j]) {
					teamNum++;
					markTeams(i, j, teamNum);
				}
			}
		}


		for(int t = 1; t <= K; t++) {
			moveTeams(); // 이동
			countScore(t); // 점수 체크
		}

		System.out.println(answer);

	}


	static void markTeams(int x, int y, int teamNum) {
		if(x < 0 || y < 0 || x >= N || y >= N || visited[x][y]) return;

		visited[x][y] = true;

		if(board[x][y] == 0) return;
		if(board[x][y] != 4) { // 각 번호별 팀 정보를 저장
			Pair p = new Pair(x, y, board[x][y]);
			ArrayList<Pair> temp = teams.getOrDefault(teamNum, new ArrayList<Pair>());
			temp.add(p);
			Collections.sort(temp);
			teams.put(teamNum, temp);	
		}

		marking[x][y] = teamNum;

		for(int d = 0; d < 4; d++) {
			markTeams(x+dxy[d][0], y+dxy[d][1], teamNum);
		}
	}

	// ArrayList 앞 원소의 좌표를 가져오면됨.
	static void moveTeams() {		
		Set<Integer> keys = teams.keySet();
		for(Integer key: keys) { // key = teamNum
			ArrayList<Pair> team = teams.get(key);

			for(int n = team.size() - 1; n >= 0; n--) {
				Pair pair = team.get(n);
				if(n == 0) {
					for(int d = 0; d < 4; d++) {
						int nx = pair.x + dxy[d][0];
						int ny = pair.y + dxy[d][1];

						if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;

						if(marking[nx][ny] == key) {
							board[nx][ny] = 1;
							pair.x = nx;
							pair.y = ny;
						}

					}
				}else {
					Pair prePair = team.get(n-1);

					//board 상에서 옮겨줌
					if(pair.num == 3)board[pair.x][pair.y] = 4;
					board[prePair.x][prePair.y] = pair.num;

					// 객체 좌표 정보 동기화
					pair.x = prePair.x;
					pair.y = prePair.y;
				}
			}
		}
	}

	static void countScore(int t) {
		t = t % (4 * N + 1); //이 이상 높은 수가 나오지 않도록 
		
		int teamNum = -1;
		int targetX = -1; 
		int targetY = -1;
		
		if(t <= N) {
			t = t - 1;
			for(int j = 0; j < N; j++) {
				int n = board[t][j];

				if(n != 0 && n != 4) { // 점수 얻는 경우
					teamNum = marking[t][j];
					targetX = t;
					targetY = j;
					break;
				}
			}

		}
		else if(t <= N*2) {
			t = (t/2) - 1;
			for(int i = N-1; i > 0; i--) {
				int n = board[i][t];

				if(n != 0 && n != 4) { // 점수 얻는 경우
					teamNum = marking[i][t];
					targetX = i;
					targetY = t;
					break;
				}
			}
		}
		else if(t <= N*3) {
			t = (t/3) - 1;
			for(int j = N-1; j > 0; j--) {
				int n = board[((N-1)-t)][j];

				if(n != 0 && n != 4) { // 점수 얻는 경우
					teamNum = marking[(N-1)-t][j];
					targetX = (N-1)-t;
					targetY = j;
					break;
				}
			}
		}
		else if(t <= N*4) {
			t = (t/4) - 1;
			for(int i = 0; i < N; i++) {
				int n = board[i][(N-1)-t];

				if(n != 0 && n != 4) { // 점수 얻는 경우
					teamNum = marking[i][(N-1)-t];
					targetX = i;
					targetY = (N-1)-t;
					break;
				}
			}
		}
		
		if(teamNum == -1) return;
		
		// 점수계산
		ArrayList<Pair> team = teams.get(teamNum);
		for(int i = 0; i < team.size(); i++) {
			Pair p = team.get(i);
			if(p.x == targetX && p.y == targetY) {
				answer += Math.pow(i+1, 2);
			}
		}
		
		// 머리 꼬리 바꾸기
		team.get(0).num = 3;
		team.get(team.size()-1).num = 1;
		Collections.reverse(team);
	}
}