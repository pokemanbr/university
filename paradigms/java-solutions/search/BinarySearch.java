package search;

public class BinarySearch {

    /*
        grow(arr): for(i = 0..arr.length - 2 : arr[i] >= arr[i + 1])

        result(answer): (0 <= answer && answer < arr.length) && arr[answer] <= value && (answer == 0 || arr[answer] > value)
     */

    //pred: args.length > 0 && (args[i] is a number)
    public static void main(String[] args) {

        //pred: true
        int value = Integer.parseInt(args[0]);
        //post: value = Integer.parseInt(args[0])

        //pred: grow(arr)
        int[] arr = new int[args.length - 1];
        //post: arr = new int[args.length - 1]

        //pred: true
        int i = 1;
        //post: i == 1
        
        //pred: args.length
        while (i < args.length) {
            
            //pred: i > 0 && i < args.length 
            arr[i - 1] = Integer.parseInt(args[i]);
            //post: arr[i - 1] = Integer.parseInt(args[i]);
            
            //pred: true
            i++;
            //post: i = i' + 1
        }
        //post: i >= args.length

        //pred: true
        RecursiveBinarySearch algo1 = new RecursiveBinarySearch();
        //post: algo = new RecursiveBinarySearch();
        
        //pred: grow(arr)
        int answer1 = algo1.search(value, arr);
        //post: result(answer1)

        //pred: true
        IterativeBinarySearch algo2 = new IterativeBinarySearch();
        //post: algo = new RecursiveBinarySearch();

        //pred: grow(arr)
        int answer2 = algo2.search(value, arr);
        //post: result(answer1)

        assert answer1 == answer2 : "The two searchers work differently";
        System.out.println(answer1);
    }
    //post: result(answer)
}
