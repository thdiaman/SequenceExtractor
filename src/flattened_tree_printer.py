import re
import ast
from collections import defaultdict

def iterate_blocks(block1, block2, pairs):
	if type(block1) is list and type(block2) is list:
		iterate_pairs(block1, pairs)
		iterate_pairs(block2, pairs)
		iterate_conditions(block1[-1], block2[0], pairs)
	elif type(block1) is list:
		iterate_pairs(block1, pairs)
		iterate_conditions(block1[-1], block2, pairs)
	elif type(block2) is list:
		iterate_pairs(block2, pairs)
		iterate_conditions(block1, block2[0], pairs)
	else:
		pairs.add((block1, block2))

def isListOfLists(l):
	return type(l) is list and type(l[0]) is list

def iterate_conditions(block1, block2, pairs):
	if isListOfLists(block1) and isListOfLists(block2):
		for s in block1:
			for t in block2:
				iterate_blocks(s, t, pairs)
	elif isListOfLists(block1):
		for s in block1:
			iterate_blocks(s, block2, pairs)
	elif isListOfLists(block2):
		for t in block2:
			iterate_blocks(block1, t, pairs)
	else:
		try:
			pairs.add((block1, block2))
		except:
			iterate_blocks(block1, block2, pairs)

def iterate_pairs(blocks, pairs):
	for block1, block2 in zip(blocks, blocks[1:]):
		if type(block1) is list or type(block2) is list:
			iterate_conditions(block1, block2, pairs)
		else:
			pairs.add((block1, block2))

def add_all_ids(blocks, last_id = 0):
	class IDClass:
		def __init__(self, unique_id):
			self.unique_id = unique_id
		def get_next_id(self):
			self.unique_id += 1
			return "a" + str(self.unique_id)
		def add_ids(self, l, list_id = 0):
			if type(l[list_id]) is list:
				for i, _ in enumerate(l[list_id]):
					self.add_ids(l[list_id], i)
			else:
				l[list_id] = (self.get_next_id(), l[list_id])
	idclass = IDClass(last_id)
	def add_the_ids(l):
		idclass.add_ids([l])
		return l
	return add_the_ids(blocks), idclass.unique_id

def remove_empty_elements(pairs):
	emptyelements = set([p for p, r in pairs if p[1] == ""])
	emptyelements = emptyelements.union([r for p, r in pairs if r[1] == ""])
	emptyelements = list(emptyelements)
	while len(emptyelements) > 0:
		emptyelement = emptyelements.pop()
		pairs_to_modify = [(p, r) for p, r in pairs if p == emptyelement or r == emptyelement]
		if len(pairs_to_modify) > 0:
			new_pairs = set()
			pairs_to_remove = set()
			for pair_to_modify in pairs_to_modify:
				pairs_to_remove.add(pair_to_modify)
				if pair_to_modify[0] == emptyelement:
					for pair in pairs:
						if pair[1] == emptyelement:
							new_pairs.add((pair[0], pair_to_modify[1]))
							pairs_to_remove.add(pair)
				else:
					for pair in pairs:
						if pair[0] == emptyelement:
							new_pairs.add((pair_to_modify[0], pair[1]))
							pairs_to_remove.add(pair)
			for pair_to_remove in pairs_to_remove:
				pairs.remove(pair_to_remove)
			pairs = pairs.union(new_pairs)
	return pairs

def replace_ids_with_hashtags(pairs):
	return set([(p[1] + "#" + p[0][1:] if p[0].startswith("a") else p, r[1] + "#" + r[0][1:] if r[0].startswith("a") else r) for p, r in pairs])

def get_paths_from_pairs(pairs):
	def getAllPaths(g, s, d):
		def getAllPathsUtil(g, u, d, visited, path, paths):
			visited[u] = True
			path.append(u)
			if u == d:
				paths.append(list(path))
			else:
				for i in g[u]:
					if visited[i]==False:
						getAllPathsUtil(g, i, d, visited, path, paths)
			path.pop()
			visited[u] = False
		visited = defaultdict(lambda: False)
		path = []
		paths = []
		getAllPathsUtil(g, s, d, visited, path, paths)
		return paths

	allelements = set([p for p, r in pairs])
	allelements = allelements.union([r for p, r in pairs])
	g = defaultdict(list)
	for p, r in pairs:
		g[p].append(r)
	return [apath[1:-1] for apath in getAllPaths(g, 'START', 'END')]

def get_paths(content, add_unique_ids):
	content = re.sub("(\s|\[)(\w|\(|\))", r'\1"\2', content)
	content = re.sub("(\w|\(|\))(,|\])", r'\1"\2', content)
	content = re.sub("(\[)(\])", r'\1""\2', content)
	try:
		blocks = ast.literal_eval(content)
		if type(blocks) is tuple:
			blocks = list(blocks)
	except:
		blocks = []
	theblocks = blocks
	allpaths = []
	last_id = 0
	for block in theblocks:
		blocks, last_id = add_all_ids([block], last_id)
		pairs = set()
		blocks = ['START'] + blocks + ['END']
		iterate_pairs(blocks, pairs)

		pairs = remove_empty_elements(pairs)
		pairs = replace_ids_with_hashtags(pairs)

		paths = get_paths_from_pairs(pairs)
		if not add_unique_ids:
			for i, path in enumerate(paths):
				paths[i] = [p.split('#')[0] for p in path]
		allpaths += paths

	# Convert to java object
	import java.util.ArrayList as ArrayList
	import java.lang.String as String
	javaPaths = ArrayList()
	for path in allpaths:
		javaPath = ArrayList()
		for elem in path:
			javaPath.add(String(elem))
		javaPaths.add(javaPath)

	return javaPaths
