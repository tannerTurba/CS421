partition :: [a] -> Int -> [[a]]
partition lst n
  | n <= 0 = partition lst 1
  | length lst <= n = [lst]
  | otherwise = [take n lst] ++ partition (drop n lst) n

count :: (Eq a) => a -> [a] -> Int
count _ [] = 0
count y (x:xs) =
  if y == x then 1 + count y xs
  else count y xs

removeAll :: (Eq a) => a -> [a] -> [a]
removeAll val lst = [a | a <- lst, (a/=val)]

counts :: (Eq a) => [a] -> [(a,Int)]
counts [] = []
counts (l:ist) = [(l, length([a | a <- (l:ist), (a == l)]))] ++ counts([a | a <- (l:ist), (a /= l)])

