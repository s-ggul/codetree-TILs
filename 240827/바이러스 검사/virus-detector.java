import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
	static int[] customers;
	static int[] maxCheckNum = new int[2];
	static int N;
	static long answer;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// 입력데이터 전처리
		N = Integer.parseInt(br.readLine()); // 가게 수
		answer = N; // 팀장 포함 
		
		customers = new int[N];		

		// 가게 별 손님 수 (피검사자)
		StringTokenizer st = new StringTokenizer(br.readLine());
		for(int i = 0; i < N; i++) {
			customers[i] = Integer.parseInt(st.nextToken());
		}

		st = new StringTokenizer(br.readLine());
		maxCheckNum[0] = Integer.parseInt(st.nextToken()); // 검사 팀장의 최대치
		maxCheckNum[1] = Integer.parseInt(st.nextToken()); // 검사 팀원의 최대치
	
		excludeLeadersNum(); // 입력 초기 팀장에 대한 부분은 제외시킴.
		checkNumOfCustomers();
		
		System.out.println(answer);
	}
 
	static void excludeLeadersNum() {
		for(int i = 0; i < N; i++) {
			customers[i] -= maxCheckNum[0];
		}
	}
	
	static void checkNumOfCustomers() {
		for(int i = 0; i < N; i++) {
			if(customers[i] <= 0) continue;
			else if(customers[i] <= maxCheckNum[1]) answer += 1;
			else {
				answer += (customers[i] / maxCheckNum[1]);
				if(customers[i] % maxCheckNum[1] != 0) answer += 1;
			}
		}
		
	}
}