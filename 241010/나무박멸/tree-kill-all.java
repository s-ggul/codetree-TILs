import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

class TreeGroup {
	int x, y;
	int treeNum;
	
	public TreeGroup(int x, int y, int treeNum) {
		this.x = x;
		this.y = y;
		this.treeNum = treeNum;
	}	
}
public class Main {
	static int N, M, K, C, answer;
	static int[][] board;
	static int[][] jecho;
	
	static int[][] dxy = {{-1,0},{1,0},{0,-1},{0,1}};
	static int[][] dxy2 = {{-1,-1},{1,1},{1,-1},{-1,1}};
	static ArrayList<TreeGroup> trees = new ArrayList<>();
	static ArrayList<Integer> spaceCount;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		
		board = new int[N][N];
		jecho = new int[N][N];
		answer = 0;
		
		for(int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < N; j++) {
				board[i][j] = Integer.parseInt(st.nextToken());
				
				if(board[i][j] != 0 && board[i][j] != -1) {
					trees.add(new TreeGroup(i, j, board[i][j]));
				}
			}
		}
		
		simulation();
		
		System.out.println(answer);
		
	}
	
	static void simulation() { // 전체 시뮬레이션
		
		for(int t = 0; t < M; t++) {
			spaceCount = new ArrayList<>();		
			
			growTree(); // 나무 성장
			widenTree(); // 나무 확장
			searchRemoveTree();
		}
	}
	
	static void growTree() { 
		
		// 인접한 네개의 칸에 대해서 나무가 있다면 그만큼 성장 최대 4
		for(int i = 0; i < trees.size(); i++) {
			int spaceCnt = 0;
			int adjTreeCnt = 0;
			for(int d = 0; d < 4; d++) {
				int nx = trees.get(i).x + dxy[d][0];
				int ny = trees.get(i).y + dxy[d][1];
				if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
				if(board[nx][ny] == 0 && jecho[nx][ny] == 0){
					spaceCnt++;
				}
				if(board[nx][ny] > 0) {
					adjTreeCnt++;
				}
			}	
			
			board[trees.get(i).x][trees.get(i).y] += adjTreeCnt;
			trees.get(i).treeNum += adjTreeCnt;
			spaceCount.add(spaceCnt);
		}
	}
	
	static void widenTree() { // 나무 확장
		int[][] tempBoard = new int[N][N];
		
		// 나무 주변 빈칸(나무 없고, 장애물 없고, 제초제 없는)
		// 만일 두 나무에서 한곳에 확장을 동시에 한다면 더해줌
		for(int i = 0; i < trees.size(); i++) {
			
			if(spaceCount.get(i) == 0) continue;
			
			
			TreeGroup tree = trees.get(i);	
			int childTreeNum = tree.treeNum / spaceCount.get(i);  
			
			for(int d = 0; d < 4; d++) {
				int nx = tree.x + dxy[d][0];
				int ny = tree.y + dxy[d][1];
				
				if(nx < 0 || ny < 0 || nx >= N || ny >= N ) continue;
				if(jecho[nx][ny] > 0) continue; // 제초제 없는지 체크
				if(board[nx][ny] != 0) continue; // 다른 나무가 없는 곳은 아닌지 체크
				
				tempBoard[nx][ny] += childTreeNum;
			}
		}
		
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(tempBoard[i][j] != 0) {
					board[i][j] = tempBoard[i][j];
					trees.add(new TreeGroup(i, j, tempBoard[i][j]));
				}
			}
		}
	}
	
	static void discountJecho() {
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(jecho[i][j] > 0) jecho[i][j] -= 1;
			}
		}
	}
	
	static void searchRemoveTree() { // 대각 방향의 제거할 나무 수를 찾는 메서드
		//제초제가 있는 칸은 C년이후에 사라짐 제초제에 대한 테이블 따로 관리
		// 만약 박멸시키는 나무의 수가 동일한 칸이 있는 경우에는 행이 작은 순서대로, 만약 행이 같은 경우에는 열이 작은 칸에 제초제를 뿌리게 됨
		int maxNum = -1;
		TreeGroup maxNumTree = null;
		for(int i = 0; i < trees.size(); i++) {
			TreeGroup tree = trees.get(i);
			int x = tree.x;
			int y = tree.y;
			
			if(board[x][y] > 0 && jecho[x][y] == 0) {
				
				int cnt = countRemovableTree(x, y);
				if(cnt > maxNum) {
					maxNum = cnt;
					maxNumTree = tree;
				}
				else if(cnt == maxNum) {
					if(maxNumTree.x == tree.x) {
						maxNumTree = maxNumTree.y < tree.y ? maxNumTree : tree;
					}
					else maxNumTree = maxNumTree.x < tree.x ? maxNumTree : tree;
				}
			}
		}
		
		if(maxNumTree != null) removeTree(maxNumTree.x, maxNumTree.y);
	}
	
	static int countRemovableTree(int x, int y) { 
		int cnt = board[x][y];
		
		for(int d = 0; d < 4; d++) {
			int nx = x;
			int ny = y;
			for(int i = 0; i < K; i++) {
				nx += dxy2[d][0];
				ny += dxy2[d][1];
				
				if(nx < 0 || ny < 0 || nx >= N || ny >= N ) break;
				if(board[nx][ny] == -1 || board[nx][ny] == 0) break;
				
				cnt += board[nx][ny];
			}
		}
		
		return cnt;
	}
	
	static void removeTree(int x, int y) { 
		discountJecho();
		
		int numOfRemovedTree = board[x][y];
		board[x][y] = 0;
		removeTreeFromTrees(x,y);
		jecho[x][y] = C;
		
		for(int d = 0; d < 4; d++) {
			int nx = x;
			int ny = y;
			for(int i = 0; i < K; i++) {
				nx += dxy2[d][0];
				ny += dxy2[d][1];
				
				if(nx < 0 || ny < 0 || nx >= N || ny >= N ) break;
				if(board[nx][ny] == -1 || board[nx][ny] == 0) {
					jecho[nx][ny] = C;
					break;
				}
				
				numOfRemovedTree += board[nx][ny];
				board[nx][ny] = 0;
				jecho[nx][ny] = C;
				removeTreeFromTrees(nx, ny);
			}
		}
		answer += numOfRemovedTree;
	}
	
	static void removeTreeFromTrees(int x, int y) {
		for(int i = 0; i < trees.size(); i++) {
			TreeGroup tree = trees.get(i);
			if(tree.x == x && tree.y == y)trees.remove(i);
		}
	}
}