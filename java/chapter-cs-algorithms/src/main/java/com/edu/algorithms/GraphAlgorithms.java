package com.edu.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * CS 알고리즘 트랙 - 그래프(Graph): 표현, 탐색(BFS/DFS), 최단경로(Dijkstra)
 *
 * 그래프는 "정점(vertex)"들이 "간선(edge)"으로 연결된 관계망이다.
 * 지하철 노선도, SNS 친구관계, 도로망, 웹 링크... 세상의 연결은 대부분 그래프다.
 *
 * 표현 방법 두 가지:
 *   1) 인접 리스트(adjacency list): 각 정점마다 '이웃 목록'을 저장.
 *      - 공간 O(V + E). 간선이 적은(희소) 그래프에 효율적. (대부분 이걸 쓴다)
 *   2) 인접 행렬(adjacency matrix): V x V 표에 연결 여부/가중치를 저장.
 *      - 공간 O(V^2). 두 정점의 연결 확인이 O(1)이지만 메모리 낭비 가능.
 *
 * 탐색 두 가지:
 *   - BFS(너비 우선): 큐(FIFO) 사용. 가까운 정점부터 물결처럼 퍼짐.
 *                    가중치 없는 그래프의 '최단 경로(간선 수)'를 준다.
 *   - DFS(깊이 우선): 스택/재귀 사용. 한 방향으로 끝까지 파고든 뒤 되돌아온다.
 *
 * 최단 경로(가중치 있는 그래프):
 *   - 다익스트라(Dijkstra): 우선순위 큐(최소 힙)로 '지금까지 가장 가까운 정점'을
 *     계속 확정해 나간다. 음수 간선이 없을 때 O((V+E) log V).
 */
public class GraphAlgorithms {

    // ── 인접 리스트 그래프 (가중치 있는 방향/무방향 겸용) ──
    static class Graph {
        // 간선: (도착 정점 to, 가중치 weight)
        static class Edge {
            int to, weight;
            Edge(int to, int weight) { this.to = to; this.weight = weight; }
        }

        private final int n;                        // 정점 수 (0..n-1)
        private final List<List<Edge>> adj;         // adj.get(v) = v의 이웃 목록

        Graph(int n) {
            this.n = n;
            adj = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }

        // 무방향 간선: 양쪽에 서로를 추가
        void addUndirectedEdge(int u, int v, int w) {
            adj.get(u).add(new Edge(v, w));
            adj.get(v).add(new Edge(u, w));
        }

        List<Edge> neighbors(int v) { return adj.get(v); }
        int size() { return n; }

        // ── BFS: 큐로 가까운 정점부터 방문 ──
        // 방문 순서를 반환. 가중치 무시(간선 1개 = 거리 1).
        List<Integer> bfs(int start) {
            List<Integer> order = new ArrayList<>();
            boolean[] visited = new boolean[n];
            Queue<Integer> queue = new LinkedList<>();  // FIFO 큐
            visited[start] = true;
            queue.add(start);
            while (!queue.isEmpty()) {
                int cur = queue.poll();                 // 가장 먼저 들어온 것부터
                order.add(cur);
                for (Edge e : adj.get(cur)) {           // 이웃들을 큐에 넣음
                    if (!visited[e.to]) {
                        visited[e.to] = true;           // 큐에 넣을 때 방문표시(중복 방지)
                        queue.add(e.to);
                    }
                }
            }
            return order;
        }

        // ── DFS: 재귀(=콜 스택)로 한 방향 끝까지 파고듦 ──
        List<Integer> dfs(int start) {
            List<Integer> order = new ArrayList<>();
            boolean[] visited = new boolean[n];
            dfsVisit(start, visited, order);
            return order;
        }
        private void dfsVisit(int v, boolean[] visited, List<Integer> order) {
            visited[v] = true;
            order.add(v);
            for (Edge e : adj.get(v)) {
                if (!visited[e.to]) dfsVisit(e.to, visited, order); // 이웃으로 더 깊이
            }
        }

        // ── 다익스트라: start에서 모든 정점까지의 최단 거리 ──
        // 우선순위 큐(최소 힙)에 (거리, 정점)을 넣고, 가장 가까운 정점부터 확정한다.
        int[] dijkstra(int start) {
            int[] dist = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);   // 아직 도달 못함 = 무한대
            dist[start] = 0;

            // (거리, 정점). 거리가 작은 것이 먼저 나오도록 최소 힙 구성
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
            pq.add(new int[]{0, start});

            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int d = top[0], u = top[1];
                if (d > dist[u]) continue;          // 이미 더 짧은 경로로 확정됨 → 낡은 항목 무시
                for (Edge e : adj.get(u)) {
                    int nd = d + e.weight;          // u를 거쳐 e.to로 가는 거리
                    if (nd < dist[e.to]) {          // 더 짧으면 갱신(완화, relaxation)
                        dist[e.to] = nd;
                        pq.add(new int[]{nd, e.to});
                    }
                }
            }
            return dist;
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" CS 알고리즘: 그래프 (BFS / DFS / 다익스트라)");
        System.out.println("=================================================\n");

        traversalDemo();
        dijkstraDemo();
        conclusion();
    }

    // ──────────────────────────────────────────────
    // BFS / DFS 시연 (가중치 무시한 무방향 그래프)
    // ──────────────────────────────────────────────
    static void traversalDemo() {
        System.out.println("── 1. BFS / DFS 순회 ──");
        System.out.println("  그래프(무방향):");
        System.out.println("      0 --- 1 --- 3");
        System.out.println("      |     |     |");
        System.out.println("      2 --- 4 --- 5");
        System.out.println("  정점: 0~5, 간선: 0-1,0-2,1-3,1-4,2-4,3-5,4-5\n");

        Graph g = new Graph(6);
        g.addUndirectedEdge(0, 1, 1);
        g.addUndirectedEdge(0, 2, 1);
        g.addUndirectedEdge(1, 3, 1);
        g.addUndirectedEdge(1, 4, 1);
        g.addUndirectedEdge(2, 4, 1);
        g.addUndirectedEdge(3, 5, 1);
        g.addUndirectedEdge(4, 5, 1);

        System.out.println("  BFS(0) 방문순서 = " + g.bfs(0));
        System.out.println("    → 큐(FIFO): 0의 이웃(1,2) 먼저, 그 다음 층(3,4)... 물결처럼 퍼짐.");
        System.out.println("  DFS(0) 방문순서 = " + g.dfs(0));
        System.out.println("    → 재귀(스택): 0→1→3→5→4→2 처럼 한 길로 끝까지 파고든 뒤 되돌아옴.");
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // 다익스트라 최단경로 시연 (가중치 있는 그래프)
    // ──────────────────────────────────────────────
    static void dijkstraDemo() {
        System.out.println("── 2. 다익스트라 최단 경로 (가중치 그래프) ──");
        System.out.println("  도시 0~4, 간선의 숫자는 '거리(가중치)':");
        System.out.println("        (0)");
        System.out.println("       4/   \\1");
        System.out.println("      (1)   (2)");
        System.out.println("     1/ \\5  /2");
        System.out.println("    (3)  (4)");
        System.out.println("  간선: 0-1(4), 0-2(1), 2-1(2), 1-3(1), 1-4(5), 2-4(8)\n");

        Graph g = new Graph(5);
        g.addUndirectedEdge(0, 1, 4);
        g.addUndirectedEdge(0, 2, 1);
        g.addUndirectedEdge(2, 1, 2);
        g.addUndirectedEdge(1, 3, 1);
        g.addUndirectedEdge(1, 4, 5);
        g.addUndirectedEdge(2, 4, 8);

        int[] dist = g.dijkstra(0);
        System.out.println("  출발지 0에서 각 정점까지 최단 거리:");
        for (int v = 0; v < dist.length; v++) {
            System.out.printf("    0 → %d : %d%n", v, dist[v]);
        }
        System.out.println("\n  핵심 관찰) 0→1 직선은 4지만, 0→2(1)→1(2) = 3 이 더 짧다!");
        System.out.println("    다익스트라는 '가장 가까운 정점부터 확정'하며 이런 우회 최단경로를 찾아낸다.");
        System.out.println("    우선순위 큐(최소 힙) 덕분에 O((V+E) log V)로 빠르게 동작.");
        System.out.println("    (단, 음수 가중치가 있으면 다익스트라는 틀림 → 벨만-포드 등 다른 알고리즘 필요)\n");
    }

    static void conclusion() {
        System.out.println("── 정리 ──");
        System.out.println("  표현 : 인접 리스트(희소 그래프, O(V+E)) / 인접 행렬(연결확인 O(1), O(V^2) 공간).");
        System.out.println("  BFS  : 큐(FIFO). 가중치 없는 최단경로(간선 수). DataStructuresFromScratch의 큐가 여기 쓰인다.");
        System.out.println("  DFS  : 스택/재귀. 연결성·사이클·위상정렬 등에 활용.");
        System.out.println("  다익스트라: 우선순위 큐(TreeAndHeap의 힙)로 양의 가중치 최단경로. O((V+E) log V).");
        System.out.println();
    }
}
