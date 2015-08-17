-- invalidateAll.lua

local keys = redis.call("keys",KEYS[1])
if next(keys) ~= nil then
    return redis.call("del",unpack(keys))
end

