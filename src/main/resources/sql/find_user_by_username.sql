SELECT acc.username, acc.password, acc.email, acc.sex, acc.birthDate, auth.authority
FROM accounts acc
LEFT OUTER JOIN authorities auth
ON acc.username = auth.accountUsername
WHERE acc.username = ?;