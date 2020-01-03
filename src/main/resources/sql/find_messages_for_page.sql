SELECT id, topicId, accountUsername, date, text
FROM messages
WHERE topicId = ?
ORDER BY date, id
LIMIT ?, ?