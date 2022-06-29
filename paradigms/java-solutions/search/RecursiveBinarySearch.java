package search;

public class RecursiveBinarySearch {

    /*
        grow(arr): for(i = 0..arr.length - 2 : arr[i] >= arr[i + 1])

        invariant(left, right): (left == -1 || arr[left] > value) && (right == arr.length || arr[right] <= value)

        inside(left, mid, right): (left <= mid && mid < right)
            if left < right && mid == (left + right) / 2:
            left <= (left + right) / 2       (left + right) / 2 < right
            left / 2 <= right / 2            left / 2 < right / 2
            left <= right - true             left < right - true

        result(answer): (0 <= answer && answer < arr.length) && arr[answer] <= value && (answer == 0 || arr[answer] > value)
     */

    //pred: grow(arr)
    public int search(int value, int[] arr) {
        return search(value, arr, -1, arr.length);
    }
    //post: result(answer)

    //pred: grow(arr) && left' < right' && invariant(left', right') && inside(left', (left' + right') / 2, right')
    private int search(int value, int[] arr, int left, int right) {

        //pred: left' < right' && invariant(left', right') && inside(left', (left' + right') / 2, right')
        if (right - left > 1) {
            
            ///pred: right' - left' > 1 && invariant(left', right') && inside(left', (left + right) / 2, right')
            int mid = (left + right) / 2;
            //post: mid == (left' + right') / 2 && inside(left', mid, right')

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', mid, right')
            if (arr[mid] <= value) {
                //pred: right' - left' > 1 && invariant(mid, right') && inside(left', mid, right')
                return search(value, arr, left, mid);
                //post: inside(left', left'', right') && invariant(left'', right')
            } else {
                //pred: invariant(left', mid) && inside(left', mid, right')
                return search(value, arr, mid, right);
                //post: inside(left', right'', right') && invariant(left', right'')
            }
            //post: left' < right' && invariant(left', right') && inside(left', (left' + right') / 2, right')
        } else {
            //pred: left' + 1 == right' && invariant(left', right') && arr[left'] < value && value <= arr[right'] && grow(arr)
            return right;
            //post: result(right)
        }
        //post: left' < right' && invariant(left', right') && inside(left', (left' + right') / 2, right')
    }
    //post: result(answer)
}
