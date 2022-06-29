package search;

public class BinarySearchMin {

    /*
        decInc(args): ind: for (i = 1..ind : args[i - 1] > args[i]) && for (i = ind..args.length - 2 : args[i] < args[i + 1])

        result(answer): for (i = 0..arr.length - 1 : arr[answer] <= arr[i])
                        && (for(i = 0..arr.length - 1 : k += (arr[answer] == arr[i])) : k > 0)
     */

    //pred: args.length > 0 && for(i = 0..args.length - 1 : args[i] == (int))
    public static void main(String[] args) {

        //pred: decInc(args) && args.length > 0
        int[] arr = new int[args.length];
        //post: arr = new int[args.length]

        //pred: true
        int i = 0;
        //post: i == 0

        //pred: args.length
        while (i < args.length) {

            //pred: i > 0 && i < args.length && args[i] == (int)
            arr[i] = Integer.parseInt(args[i]);
            //post: arr[i] = Integer.parseInt(args[i]);

            //pred: true
            i++;
            //post: i = i' + 1
        }
        //post: i >= args.length

        //pred: decInc(arr)
        int answer1 = searchIterative(arr);
        //post: result(answer1)

        //pred: decInc(arr)
        int answer2 = searchRecursive(arr);
        //post: result(answer2)

        assert answer1 == answer2 : "The two searchers work differently";
        System.out.println(answer1);
    }
    //post: result(answer)

    /*
        invariant(left, right): (left == -1 || arr[left] > arr[left + 1]) && (right == 0 || arr[right - 1] < arr[right])

        inside(left, mid, right): (left < mid && mid < right)
            if right - left > 1 && mid == (left + right) / 2:
            left < (left + right) / 2       (left + right) / 2 < right
            left / 2 < right / 2            left / 2 < right / 2
            left < right - true             left < right - true
     */

    //pred: decInc(arr)
    private static int searchIterative(int[] arr) {

        //pred: true
        int left = -1, right = arr.length - 1;
        //post: left == -1 && right == arr.length - 1 && left' == left && right' == right

        //inv: left' < right' && invariant(left', right')
        while (right - left > 1) {

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', (left + right) / 2, right')
            int mid = (left + right) / 2;
            //post: mid == (left' + right') / 2 && inside(left', mid, right')

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', mid, right')
            if (arr[mid] > arr[mid + 1]) {
                //pred: right' - left' > 1 && invariant(mid, right') && inside(left', mid, right')
                left = mid;
                //post: left' == mid && left' < right' && invariant(left', right')
            } else {
                //pred: right' - left' > 1 && invariant(left', mid) && inside(left', mid, right')
                right = mid;
                //post: right' == mid && left' < right' && invariant(left', right')
            }
            //post: left' < right' && invariant(left', right')
        }
        //post: left' + 1 == right' && invariant(left', right')

        //pred: left' + 1 == right' && invariant(left', right') && arr[left'] > arr[right'] && decInc(arr)
        return arr[right];
        //post: result(right)
    }
    //post: result(answer)

    //pred: decInc(arr)
    private static int searchRecursive(int[] arr) {
        //pred: decInc(arr) && left == -1 && right == arr.length - 1
        return searchRecursive(arr, -1, arr.length - 1);
        //post: result(searchRecursive(arr, -1, arr.length - 1))
    }
    //post: result(answer)

    //pred: decInc(arr) && left' < right' && invariant(left', right') && -1 <= left && right <= arr.length - 1
    private static int searchRecursive(int[] arr, int left, int right) {
        //pred: left' == left && right' == right

        //pred: left' < right' && invariant(left', right')
        if (right - left > 1) {

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', (left + right) / 2, right')
            int mid = (left + right) / 2;
            //post: mid == (left' + right') / 2 && inside(left', mid, right')

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', mid, right')
            if (arr[mid] > arr[mid + 1]) {
                //pred: right' - left' > 1 && invariant(mid, right') && inside(left', mid, right')
                return searchRecursive(arr, mid, right);
                //post: left' == mid && left' < right' && invariant(left', right')
            } else {
                //pred: right' - left' > 1 && invariant(left', mid) && inside(left', mid, right')
                return searchRecursive(arr, left, mid);
                //post: right' == mid && left' < right' && invariant(left', right')
            }
            //post: left' < right' && invariant(left', right')
        } else {
            ///pred: left' + 1 == right' && invariant(left', right') && arr[left'] > arr[right'] && decInc(arr)
            return arr[right];
            //post: result(right)
        }
        //post: left' < right' && invariant(left', right')
    }
    //post: result(answer)
}
