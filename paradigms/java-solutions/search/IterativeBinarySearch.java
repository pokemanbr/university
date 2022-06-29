package search;

public class IterativeBinarySearch {

    /*
        grow(arr): for(i = 0..arr.length - 2 : arr[i] >= arr[i + 1])

        invariant(left, right): (right == arr.length || arr[right] <= value) && (left == -1 || arr[left] > value)

        inside(left, mid, right): (left <= mid && mid < right)
            if left < right && mid == (left + right) / 2:
            left <= (left + right) / 2       (left + right) / 2 < right
            left / 2 <= right / 2            left / 2 < right / 2
            left <= right - true             left < right - true

        result(answer): (0 <= answer && answer < arr.length) && arr[answer] <= value && (answer == 0 || arr[answer] > value)
     */

    //pred: grow(arr)
    public int search(int value, int[] arr) {
        //pred: true
        int left = -1, right = arr.length;
        //post: left == -1 && right == arr.length && left' == left && right' == right

        //inv: right' > left' && invariant(left', right') && inside(left', (left' + right') / 2, right')
        while (right - left > 1) {
            
            //pred: right' - left' > 1 && invariant(left', right') && inside(left', (left + right) / 2, right')
            int mid = (left + right) / 2;
            //post: mid == (left' + right') / 2 && inside(left', mid, right')

            //pred: right' - left' > 1 && invariant(left', right') && inside(left', mid, right')
            if (arr[mid] <= value) {
                //pred: right' - left' > 1 && invariant(left', mid) && inside(left', mid, right')
                right = mid;
                //pred: inside(left', right'', right') && invariant(left', right'')
            } else {
                //pred: right' - left' > 1 && invariant(mid, right') && inside(left', mid, right')
                left = mid;
                //pred: inside(left', left'', right') && invariant(left'', right')
            }
            //post: left' < right' && invariant(left', right') && inside(left', (left' + right') / 2, right')
        }
        //post: left' + 1 == right' && invariant(left', right') && inside(left', (left' + right') / 2, right')

        //pred: left' + 1 == right' && invariant(left', right') && arr[left'] < value && value <= arr[right'] && grow(arr)
        return right;
        //post: result(answer)
    }
    //post: result(answer)
}
